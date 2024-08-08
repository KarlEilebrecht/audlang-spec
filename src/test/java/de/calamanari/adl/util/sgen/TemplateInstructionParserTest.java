//@formatter:off
/*
 * TemplateInstructionParserTest
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class TemplateInstructionParserTest {

    @Test
    void testSpecialCases() {

        TemplateInstructionParser tip = new TemplateInstructionParser();

        assertThrows(TemplateParseException.class, () -> tip.parse("${bla"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${}"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${bad:bad}"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${UNKNOWN:}"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${UNKNOWN:unexpected}"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${OPAQUE_EXPRESSION:unexpected}"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${COMPOSITE_EXPRESSION:unexpected}"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${EXPRESSION}"));
        assertThrows(TemplateParseException.class, () -> tip.parse("${EXPRESSION:}"));

        assertEquals(IllegalArgumentException.class, new TemplateParseException("msg", new IllegalArgumentException()).getCause().getClass());

        assertTrue(tip.parse("$bla").get(0) instanceof AppendTextInstruction);
        assertTrue(tip.parse("a{bla").get(0) instanceof AppendTextInstruction);

        List<TemplateInstruction> instructions = tip.parse("${EXPRESSION!:idFilter}");

        assertEquals(1, instructions.size());

        assertTrue(instructions.get(0) instanceof OutputLimitOverrideTemplateInstruction);
        assertEquals("EXPRESSION:idFilter", instructions.get(0).toString());

    }

}
