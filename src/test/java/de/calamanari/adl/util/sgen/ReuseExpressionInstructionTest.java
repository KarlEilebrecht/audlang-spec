//@formatter:off
/*
 * IdFilterReuseExpressionInstructionTest
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class ReuseExpressionInstructionTest {

    @Test
    void testSpecialCaseReferenceNotFound() {

        IdFilterReuseExpressionInstruction instruction = new IdFilterReuseExpressionInstruction("someId");

        instruction.setBaseListSupplier(() -> Collections.emptyList());

        List<SampleExpression> res = instruction.apply(new SampleExpression("id", "label", "", false, false, false, new SampleGenInfo()));

        assertEquals(1, res.size());
        assertTrue(res.get(0).invalid());
        assertTrue(res.get(0).skip());

    }

    @Test
    void testSpecialCases() {

        assertEquals("OPAQUE_EXPRESSION", new ReuseOpaqueExpressionInstruction().toString());
        assertEquals("COMPOSITE_EXPRESSION", new ReuseCompositeExpressionInstruction().toString());

        AbstractReuseExpressionInstruction t = new AbstractReuseExpressionInstruction(null) {
            // just to test constructor
        };

        assertNotNull(t.filter);
        List<SampleExpression> list = new ArrayList<>();
        list.add(new SampleExpression("id", "label", ""));

        assertEquals(list, t.filter.apply(list));

    }

}
