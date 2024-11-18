//@formatter:off
/*
 * ReuseOpaqueExpressionInstruction
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

import java.util.function.Predicate;

/**
 * Randomly picks a previously generated opaque expression (no AND/OR) and appends it to the current expression.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class ReuseOpaqueExpressionInstruction extends AbstractReuseExpressionInstruction {

    public ReuseOpaqueExpressionInstruction() {
        super(baseList -> baseList.stream().filter(Predicate.not(SampleExpression::invalid)).filter(Predicate.not(SampleExpression::composite)).toList());
    }

    @Override
    public String toString() {
        return "OPAQUE_EXPRESSION";
    }

}
