//@formatter:off
/*
 * SampleExpressionGroupTest
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class SampleExpressionGroupTest {

    private String sampleGroup = """
            {
              "group": "simple-comparison-expressions",
              "samples": [
                {
                  "id": "id1",
                  "label": "label 1",
                  "expression": "expression 1"
                },
                {
                  "id": "id2",
                  "label": "label 2",
                  "expression": "expression 2"
                },
                {
                  "id": "cmpStrictNotEqualsReference",
                  "label": "strict not equals infix reference comparison",
                  "expression": "strict not equals expression",
                  "composite": true,
                  "generation_info": {
                    "cnt_all": 5,
                    "cnt_none": 11,
                    "cnt_is": 10,
                    "cnt_not": 12,
                    "cnt_unknown": 14,
                    "cnt_strict": 13,
                    "cnt_any": 6,
                    "bound_values": [
                      1,
                      2,
                      3
                    ],
                    "operators": [
                      "EQUALS",
                      "NOT_EQUALS",
                      "LESS_THAN",
                      "GREATER_THAN",
                      "LESS_THAN_OR_EQUALS",
                      "GREATER_THAN_OR_EQUALS"
                    ],
                    "arg_names": [
                      "a",
                      "b"
                    ],
                    "arg_values": [
                      "q"
                    ],
                    "arg_refs": [
                      "f",
                      "g"
                    ],
                    "snippets": [
                      "snippet1",
                      "snippet3"
                    ],
                    "comments": [
                      "c1",
                      "c2"
                    ]
                  }
                }
              ]
            }""";

    @Test
    void testGroupBasics() {

        SampleExpressionGroup group = SampleExpressionGroup.fromJson(sampleGroup);

        assertEquals("simple-comparison-expressions", group.group());
        assertEquals(3, group.samples().size());

        assertEquals("label 2", group.samples().get(1).label());

        assertEquals("cmpStrictNotEqualsReference", group.samples().get(2).id());

        assertEquals("strict not equals expression", group.samples().get(2).expression());
        assertEquals(true, group.samples().get(2).composite());

        assertEquals("snippet1", group.samples().get(2).generationInfo().getSnippets().get(0));

    }

    @Test
    void testMandatoryFields() {

        List<SampleExpression> samples = SampleExpressionGroup.fromJson(sampleGroup).samples();

        List<SampleExpression> emptyList = Collections.emptyList();

        assertThrows(IllegalArgumentException.class, () -> new SampleExpressionGroup(null, samples));
        assertThrows(IllegalArgumentException.class, () -> new SampleExpressionGroup("", samples));
        assertThrows(IllegalArgumentException.class, () -> new SampleExpressionGroup("  ", samples));

        assertThrows(IllegalArgumentException.class, () -> new SampleExpressionGroup("group name", null));
        assertThrows(IllegalArgumentException.class, () -> new SampleExpressionGroup("group name", emptyList));

    }

    @Test
    void testSpecials() {

        SampleExpressionGroup group1 = SampleExpressionGroup.fromJson(sampleGroup);

        SampleExpressionGroup group2 = new SampleExpressionGroup(group1.group(), Arrays.asList(new SampleExpression("id", "label", "")));

        assertEquals(group1, group2);

        Set<SampleExpressionGroup> set = new HashSet<>();

        set.add(group1);

        assertEquals(1, set.size());

        set.add(group2);

        assertEquals(1, set.size());

        assertNotNull(group1.toJson());

        Object g1 = group1;
        Object g2 = group1;
        Object gNull = null;
        Object gSet = set;

        assertEquals(g1, g2);

        assertNotEquals(g1, gNull);

        assertNotEquals(g1, gSet);

    }

}
