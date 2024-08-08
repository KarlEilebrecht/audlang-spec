//@formatter:off
/*
 * AppendTextTemplateInstruction
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
import java.util.Arrays;
import java.util.List;

/**
 * Copies plain text from the template into the generated expression. This operation does not affect the {@link SampleGenInfo} of the expression.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class AppendTextInstruction implements TemplateInstruction {

    /**
     * static text for appending
     */
    protected final String text;

    /**
     * @param text static text piece to be appended
     */
    public AppendTextInstruction(String text) {
        this.text = text;
    }

    @Override
    public List<SampleExpression> apply(SampleExpression inputExpression, int outputLimit) {
        SampleExpression outputExpression = new SampleExpression(inputExpression.id(), inputExpression.label(), inputExpression.expression() + text,
                inputExpression.invalid(), inputExpression.composite(), false, inputExpression.generationInfo().copy());
        return new ArrayList<>(Arrays.asList(outputExpression));
    }

    @Override
    public String toString() {
        return "'" + text + "'";
    }

}
