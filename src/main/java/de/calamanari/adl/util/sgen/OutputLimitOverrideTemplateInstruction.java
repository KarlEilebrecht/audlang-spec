//@formatter:off
/*
 * OutputLimitOverrideTemplateInstruction
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
 * This wrapper allows to override the default output maximum (number of variations) of a single instruction.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class OutputLimitOverrideTemplateInstruction implements TemplateInstruction {

    /**
     * wrapped instruction
     */
    private final TemplateInstruction delegate;

    /**
     * this limit overrides the default limit of the wrapped instance
     */
    private final int overrideOutputLimit;

    /**
     * @param instruction wrapped instruction we apply the restriction to
     */
    public OutputLimitOverrideTemplateInstruction(TemplateInstruction instruction, int overrideOutputLimit) {
        this.delegate = instruction;
        this.overrideOutputLimit = overrideOutputLimit;
    }

    @Override
    public void setBaseListSupplier(Supplier<List<SampleExpression>> baseListSupplier) {
        this.delegate.setBaseListSupplier(baseListSupplier);
    }

    @Override
    public int getDefaultOutputLimit() {
        return overrideOutputLimit;
    }

    @Override
    public List<SampleExpression> apply(SampleExpression inputExpression, int outputLimit) {
        return delegate.apply(inputExpression, Math.min(outputLimit, overrideOutputLimit));
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
