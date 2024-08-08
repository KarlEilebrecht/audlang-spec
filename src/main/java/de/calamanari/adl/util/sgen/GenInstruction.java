//@formatter:off
/*
 * PreparedInstruction
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

import java.util.List;

/**
 * A {@link GenInstruction} combines a template expression with template instructions to be applied sequentially.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record GenInstruction(SampleExpression template, List<TemplateInstruction> instructions) {

    public GenInstruction {
        if (template == null || instructions == null || instructions.isEmpty()) {
            throw new IllegalArgumentException(String
                    .format("Arguments template and instructions must not be null or empty, given: template=%s, instructions=%s", template, instructions));
        }
    }

    /**
     * @param targetList the list of already generated samples
     */
    public void prepare(List<SampleExpression> targetList) {
        instructions.forEach(e -> e.setBaseListSupplier(() -> targetList));
    }

    /**
     * @return the basic empty expression to start applying instructions
     */
    public SampleExpression startExpression() {
        return new SampleExpression(template.id(), template.label(), "", template.invalid(), template.composite(), false, new SampleGenInfo(template.group()));
    }

}
