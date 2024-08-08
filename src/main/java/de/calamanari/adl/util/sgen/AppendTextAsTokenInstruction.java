//@formatter:off
/*
 * AppendTextAsTokenInstruction
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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This instruction appends plain text from the template to an expression, but counting it as the appearance of a certain token in the expression's
 * {@link SampleGenInfo}. This the generation info will be correct even for constants in a template.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class AppendTextAsTokenInstruction extends AppendTextInstruction {

    /**
     * For these instructions we can define a fixed value as effectively generated value
     */
    public static final Set<TemplateInstruction> ELIGIBLE_TOKENS = Collections
            .unmodifiableSet(new LinkedHashSet<>(Arrays.asList(StatelessInstruction.ARG_NAME, StatelessInstruction.ARG_VALUE, StatelessInstruction.ARG_REF,
                    StatelessInstruction.BOUND, StatelessInstruction.COMMENT, StatelessInstruction.OP, StatelessInstruction.SNIPPET)));

    /**
     * One of the tokens that can carry constants
     */
    private final StatelessInstruction token;

    /**
     * @param text
     */
    public AppendTextAsTokenInstruction(TemplateInstruction token, String text) {
        super(text);

        if (ELIGIBLE_TOKENS.contains(token) && token instanceof StatelessInstruction sToken) {
            this.token = sToken;
        }
        else {
            throw new IllegalArgumentException(String.format("Token not eligible for constant definition, given: token=%s, text=%s", token, text));
        }
    }

    @Override
    public List<SampleExpression> apply(SampleExpression inputExpression, int outputLimit) {
        List<SampleExpression> res = super.apply(inputExpression, 1);
        token.updateGenInfo(res.get(0).generationInfo(), text);
        return res;
    }

    @Override
    public String toString() {
        return token + ":'" + text + "'";
    }

}
