//@formatter:off
/*
 * AppendTextAsTokenInstructionTest
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class AppendTextAsTokenInstructionTest {

    @Test
    void testSpecialCases() {
        assertThrows(IllegalArgumentException.class, () -> new AppendTextAsTokenInstruction(StatelessInstruction.ALL, "fooBar"));

        AppendTextAsTokenInstruction instruction = new AppendTextAsTokenInstruction(StatelessInstruction.ARG_NAME, "fooBar");

        assertEquals("ARG_NAME:'fooBar'", instruction.toString());
    }

}
