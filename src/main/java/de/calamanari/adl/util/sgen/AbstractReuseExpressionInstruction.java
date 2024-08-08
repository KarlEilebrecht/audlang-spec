//@formatter:off
/*
 * AbstractReuseExpressionInstruction
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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common logic to include the text of an already generated {@link SampleExpression} into another one.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public abstract class AbstractReuseExpressionInstruction implements TemplateInstruction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReuseExpressionInstruction.class);

    /**
     * provided the list of all available expressions available for reuse
     */
    protected final UnaryOperator<List<SampleExpression>> filter;

    /**
     * Supplier for all expressions available for reuse
     */
    private Supplier<List<SampleExpression>> baseListSupplier = Collections::emptyList;

    /**
     * @param filter to select eligible previously generated expressions for reuse
     */
    protected AbstractReuseExpressionInstruction(UnaryOperator<List<SampleExpression>> filter) {
        this.filter = (filter == null ? (l -> l) : filter);
    }

    /**
     * Returns a list of independent variations of the inputExpression after applying one of the references
     */
    protected List<SampleExpression> applyInternal(SampleExpression inputExpression, int maxVariations) {
        Random rand = GenDataUtils.createRandomWithSeed(inputExpression);
        List<SampleExpression> res = new ArrayList<>();

        List<SampleExpression> refExpressions = pickUniqueExpressions(filter.apply(baseListSupplier.get()), rand, Math.max(maxVariations, 1));

        if (!refExpressions.isEmpty()) {

            for (SampleExpression refExpression : refExpressions) {

                String newExpressionText = inputExpression.expression();
                if (refExpression.composite()) {
                    newExpressionText = newExpressionText + "( " + refExpression.expression() + " )";
                }
                else {
                    newExpressionText = newExpressionText + refExpression.expression();
                }

                SampleExpression outputExpression = new SampleExpression(inputExpression.id(), inputExpression.label(), newExpressionText,
                        inputExpression.invalid(), inputExpression.composite(), inputExpression.skip(),
                        inputExpression.generationInfo().combine(refExpression.generationInfo()));
                res.add(outputExpression);

            }
        }
        else {
            SampleExpression fail = new SampleExpression(inputExpression.id(), inputExpression.label(), inputExpression.expression(), true,
                    inputExpression.composite(), true, inputExpression.generationInfo().copy());
            LOGGER.error("{} failed: No matching reference expression found, marking expression as invalid with skip=true: {}", this, fail);
            res.add(fail);
        }
        return res;
    }

    @Override
    public int getDefaultOutputLimit() {
        return 3;
    }

    @Override
    public List<SampleExpression> apply(SampleExpression inputExpression, int outputLimit) {
        return applyInternal(inputExpression, inputExpression.invalid() ? 0 : outputLimit);
    }

    /**
     * Sets this instances list supplier providing the full list of previously generated expressions
     */
    @Override
    public void setBaseListSupplier(Supplier<List<SampleExpression>> baseListSupplier) {
        this.baseListSupplier = baseListSupplier;
    }

    /**
     * Picks one of the patterns randomly
     * 
     * @param patterns not null, not empty
     * @param rand for making a random decising
     * @return one of the expressions or null if there is none
     */
    private static List<SampleExpression> pickUniqueExpressions(List<SampleExpression> expressions, Random rand, int max) {

        if (expressions.isEmpty()) {
            return Collections.emptyList();
        }
        else if (expressions.size() == 1 || expressions.size() <= max) {
            return expressions;
        }
        else {
            List<SampleExpression> res = new ArrayList<>();
            List<SampleExpression> availableExpressions = new ArrayList<>(expressions);
            for (int i = 0; i < max; i++) {
                SampleExpression value = availableExpressions.get(rand.nextInt(availableExpressions.size()));
                res.add(value);
                availableExpressions.remove(value);
            }

            return res;
        }
    }

}
