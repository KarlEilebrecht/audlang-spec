//@formatter:off
/*
 * InstructionPlanExecutor
 * Copyright 2024 Karl Eilebrecht
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"):
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//@formatter:on

package de.calamanari.adl.util.sgen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link InstructionPlanExecutor} executes a previously generated plan to generate samples.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class InstructionPlanExecutor {

    /**
     * To avoid combinatoric explosion, we limit the number of variations. If this is reached, all pending instructions on the current expression will only
     * produce a single result.
     */
    private static final int VARIATION_COUNT_LIMIT = 50;

    /**
     * target list for storing the generated samples for reference
     */
    private final List<SampleExpression> allGeneratedSamples = new ArrayList<>();

    /**
     * @param plan to be executed
     * @return list of generated expression groups
     */
    public List<SampleExpressionGroup> execute(GenInstructionPlan plan) {

        plan.prepare(allGeneratedSamples);

        List<SampleExpressionGroup> res = new ArrayList<>();

        for (GenInstructionGroup instructionGroup : plan.groups()) {

            List<SampleExpression> groupResultList = new ArrayList<>();

            instructionGroup.members().forEach(instruction -> processTemplateInstructions(instruction.startExpression(), instruction.instructions(),
                    groupResultList, new AtomicInteger()));

            SampleExpressionGroup sampleExpressionGroup = new SampleExpressionGroup(instructionGroup.group(), groupResultList);
            res.add(sampleExpressionGroup);

        }

        return res;

    }

    /**
     * Recursively processes the instructions for a single expression template and adds the result to the target list
     * <p>
     * Each instruction can either produce a single sample expression based on the given one or multiple (potentially spawning multiple subsequent instructions
     * recursively).
     * 
     * @param baseExpression expression to perform more instructions on
     * @param templateInstructions pending instructions
     * @param groupResultList result list
     * @param variationCount number of variations created so far
     */
    private void processTemplateInstructions(SampleExpression baseExpression, List<TemplateInstruction> templateInstructions,
            List<SampleExpression> groupResultList, AtomicInteger variationCount) {

        List<SampleExpression> results = null;
        if (variationCount.get() < VARIATION_COUNT_LIMIT) {
            results = templateInstructions.get(0).apply(baseExpression);
        }
        else {
            results = templateInstructions.get(0).apply(baseExpression, 0);
        }
        List<TemplateInstruction> furtherInstructions = templateInstructions.subList(1, templateInstructions.size());
        if (furtherInstructions.isEmpty()) {
            for (SampleExpression result : results) {
                result = finalizeSampleExpression(result);
                groupResultList.add(result);
                variationCount.incrementAndGet();

                // store final result for later reference
                allGeneratedSamples.add(result);
            }
        }
        else {
            results.forEach(instruction -> processTemplateInstructions(instruction, furtherInstructions, groupResultList, variationCount));
        }

    }

    /**
     * Replaces the generated expression's id with a unique one by appending a hash over the expression's text
     */
    private SampleExpression finalizeSampleExpression(SampleExpression expression) {
        return new SampleExpression(expression.id() + "_" + Long.toHexString(GenDataUtils.hashLong(expression.expression())), expression.label(),
                expression.expression(), expression.invalid(), expression.composite(), expression.skip(), expression.generationInfo().copy());
    }

}
