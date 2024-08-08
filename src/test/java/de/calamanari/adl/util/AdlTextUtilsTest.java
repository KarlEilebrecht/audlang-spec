//@formatter:off
/*
 * AdlTextUtilsTest
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

package de.calamanari.adl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.calamanari.adl.util.AdlTextUtils.SpecialCharacter;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class AdlTextUtilsTest {

    private static final Map<String, String> TEXT_TO_ESCAPED_TEXT_MAP;
    static {
        Map<String, String> map = new LinkedHashMap<>();

        map.put("", "");
        map.put("foo bar", "foo bar");
        map.put("\t", "<HT>");
        map.put("foo\tbar", "foo<HT>bar");
        map.put("foo \t bar", "foo <HT> bar");
        map.put("\t\n", "<HT><LF>");
        map.put("foo\t\nbar\t", "foo<HT><LF>bar<HT>");
        map.put("\nfoo \t bar", "<LF>foo <HT> bar");
        map.put("\\\t", "\\\\<HT>");
        map.put("foo\\\tbar", "foo\\\\<HT>bar");
        map.put("foo \\\t bar", "foo \\\\<HT> bar");
        map.put("\n\\\t", "<LF>\\\\<HT>");
        map.put("\\\t\\\n", "\\\\<HT>\\\\<LF>");
        map.put("\\ \t", "\\ <HT>");
        map.put("\\<BLA>", "\\<BLA>");
        map.put("<FOO>\\<BAR>", "<FOO>\\<BAR>");
        map.put("<HT>", "\\<HT>");
        map.put("\\<HT>", "\\\\\\<HT>");
        map.put("<HT", "<HT");
        map.put("\t>", "<HT>>");
        map.put("<>", "<>");
        map.put("\\\\<HT", "\\\\<HT");
        map.put("<very long text that could be a candidate>", "<very long text that could be a candidate>");
        map.put("some unicode \u0234\u0127", "some unicode \u0234\u0127");
        map.put("\u0000", "<NUL>");

        TEXT_TO_ESCAPED_TEXT_MAP = Collections.unmodifiableMap(map);

    }

    private static final Map<String, String> TEXT_TO_DQUOTED_TEXT_MAP;
    static {
        Map<String, String> map = new LinkedHashMap<>();

        map.put("", "");
        map.put("\"", "\"\"\"\"");
        map.put("argName", "argName");
        map.put("argument name", "\"argument name\"");
        map.put("a<5", "\"a<5\"");
        map.put("a>5", "\"a>5\"");
        map.put("name=\"Harry\"", "\"name=\"\"Harry\"\"\"");
        map.put("Hey!", "\"Hey!\"");
        map.put("(", "\"(\"");
        map.put("Lisa,Harry", "\"Lisa,Harry\"");
        map.put("*", "\"*\"");
        map.put("@", "\"@\"");
        map.put("@name", "\"@name\"");
        map.put("name@company.de", "name@company.de");
        map.put("http://test.com", "\"http://test.com\"");
        map.put("some\u0234\u0127", "some\u0234\u0127");

        TEXT_TO_DQUOTED_TEXT_MAP = Collections.unmodifiableMap(map);

    }

    @Test
    void testAddDoubleQuotesIfRequired() {

        for (Map.Entry<String, String> entry : TEXT_TO_DQUOTED_TEXT_MAP.entrySet()) {
            assertEquals(entry.getValue(), AdlTextUtils.addDoubleQuotesIfRequired(entry.getKey()));
        }

    }

    @Test
    void testRemoveDoubleQuotesIfRequired() {

        for (Map.Entry<String, String> entry : TEXT_TO_DQUOTED_TEXT_MAP.entrySet()) {
            assertEquals(entry.getKey(), AdlTextUtils.removeDoubleQuotesIfRequired(entry.getValue()));
        }

    }

    @Test
    void testEscape() {

        for (Map.Entry<String, String> entry : TEXT_TO_ESCAPED_TEXT_MAP.entrySet()) {
            assertEquals(entry.getValue(), AdlTextUtils.escapeSpecialCharacters(entry.getKey()));
        }

        for (SpecialCharacter spc : SpecialCharacter.values()) {

            String input = "" + (char) spc.code;

            assertEquals("<" + spc.name() + ">", AdlTextUtils.escapeSpecialCharacters(input));
        }

    }

    @Test
    void testUnescape() {

        AdlTextUtils.unescapeSpecialCharacters("\\\\<HT>");

        for (Map.Entry<String, String> entry : TEXT_TO_ESCAPED_TEXT_MAP.entrySet()) {
            assertEquals(entry.getKey(), AdlTextUtils.unescapeSpecialCharacters(entry.getValue()));
        }

        for (SpecialCharacter spc : SpecialCharacter.values()) {

            assertEquals("" + ((char) spc.code), AdlTextUtils.unescapeSpecialCharacters("<" + spc.name() + ">"));
        }

    }

    @Test
    void testSpecialCases() {

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.escapeSpecialCharacters(null));

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.unescapeSpecialCharacters(null));

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.addDoubleQuotesIfRequired(null));

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.addDoubleQuotesIfRequired("test\t "));
        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.addDoubleQuotesIfRequired("test\u0000 "));

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.removeDoubleQuotesIfRequired(null));
        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.removeDoubleQuotesIfRequired("\"test\t\""));
        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.removeDoubleQuotesIfRequired("\"test\u0000\""));

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.removeDoubleQuotesIfRequired("\""));

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.removeDoubleQuotesIfRequired("\"\"x\"\" "));

        assertThrows(IllegalArgumentException.class, () -> AdlTextUtils.removeDoubleQuotesIfRequired("\"some\"\"\"\""));

        assertNull(SpecialCharacter.resolve(-1));
        assertNull(SpecialCharacter.resolve(10000));

    }

}
