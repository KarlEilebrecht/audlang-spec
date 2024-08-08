//@formatter:off
/*
 * TemplateInstruction
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
import java.util.function.Supplier;

/**
 * A {@link TemplateInstruction} is command to be executed on a sample expression template to create one or multiple result expressions in a sequential manner.
 * <p/>
 * The output(s) are complete (valid) once all instructions in a row have been applied (left to right).
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public interface TemplateInstruction {

    /**
     * @return default maximum number of variations created by this instruction, must be &gt;=1, default is 1
     */
    default int getDefaultOutputLimit() {
        return 1;
    }

    /**
     * Sets this instances list supplier providing the full list of previously generated expressions
     */
    default void setBaseListSupplier(Supplier<List<SampleExpression>> baseListSupplier) {
        // default is a no-op
    }

    /**
     * Appends information to the expression and produces one or many output expressions.
     * 
     * @param inputExpression base expression to append tokens
     * @param outputLimit maximum number of variations, &lt;=0 means one, skip all extra variations
     * @return list with one or more output expressions
     */
    List<SampleExpression> apply(SampleExpression inputExpression, int outputLimit);

    /**
     * Method call using {@link #getDefaultOutputLimit()}
     * 
     * @param inputExpression base expression to append tokens
     * @return list with one or more output expressions
     */
    default List<SampleExpression> apply(SampleExpression inputExpression) {
        return this.apply(inputExpression, getDefaultOutputLimit());
    }
}
