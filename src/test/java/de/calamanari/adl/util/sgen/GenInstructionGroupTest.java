//@formatter:off
/*
 * GenInstructionGroupTest
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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class GenInstructionGroupTest {

    @Test
    void testSpecialCases() {

        List<GenInstruction> emptyMembers = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> new GenInstructionGroup(null, emptyMembers));
        assertThrows(IllegalArgumentException.class, () -> new GenInstructionGroup("", emptyMembers));
        assertThrows(IllegalArgumentException.class, () -> new GenInstructionGroup(" ", emptyMembers));
        assertThrows(IllegalArgumentException.class, () -> new GenInstructionGroup("group", null));

    }

}