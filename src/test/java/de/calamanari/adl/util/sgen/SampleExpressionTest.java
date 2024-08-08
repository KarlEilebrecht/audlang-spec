//@formatter:off
/*
 * SampleExpressionTest
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class SampleExpressionTest {

    static final Logger LOGGER = LoggerFactory.getLogger(SampleExpressionTest.class);

    @Test
    void testBasics1() {

        SampleExpression minimum = new SampleExpression("id", "some label", "");
        assertEquals("id", minimum.id());
        assertEquals("some label", minimum.label());
        assertEquals("", minimum.expression());
        assertEquals(false, minimum.invalid());
        assertEquals(false, minimum.composite());
        assertNull(minimum.generationInfo());

        SampleExpression otherWithSameId = new SampleExpression("id", "other label", "expression");
        assertEquals("expression", otherWithSameId.expression());
        assertEquals(minimum, otherWithSameId);

    }

    @Test
    void testBasics2() {

        SampleExpression sample = new SampleExpression("id2", "some label", "expression", true);
        assertEquals("id2", sample.id());
        assertEquals("some label", sample.label());
        assertEquals("expression", sample.expression());
        assertEquals(true, sample.invalid());
        assertEquals(false, sample.composite());
        assertNull(sample.generationInfo());

        sample = new SampleExpression("id2", "some label", "expression", false, true);
        assertEquals("id2", sample.id());
        assertEquals("some label", sample.label());
        assertEquals("expression", sample.expression());
        assertEquals(false, sample.invalid());
        assertEquals(true, sample.composite());
        assertNull(sample.generationInfo());

        sample = new SampleExpression("id3", "some label", "expression", false, true, true);
        assertEquals("id3", sample.id());
        assertEquals("some label", sample.label());
        assertEquals("expression", sample.expression());
        assertEquals(false, sample.invalid());
        assertEquals(true, sample.composite());
        assertEquals(true, sample.skip());
        assertNull(sample.generationInfo());

    }

    @Test
    void testBasicsWithInfo() {

        SampleExpression sample = new SampleExpression("id3", "some label", "expression", false, true, false, createInfo());
        assertEquals("id3", sample.id());
        assertEquals("some label", sample.label());
        assertEquals("expression", sample.expression());
        assertEquals(false, sample.invalid());
        assertEquals(true, sample.composite());
        assertInfoExpectedInitially(sample.generationInfo());

    }

    @Test
    void testJsonCopy() {

        SampleExpression minimum = new SampleExpression("id3", "some label", "expression");

        assertSampleExpressionsEqual(minimum, SampleExpression.fromJson("""
                {
                    "id" : "id3",
                    "label" : "some label",
                    "expression" : "expression"
                }
                """));

        assertSampleExpressionsEqual(minimum, SampleExpression.fromJson(minimum.toJson()));

        SampleExpression sample = new SampleExpression("id3", "some label", "expression", false, true, true, createInfo());

        assertSampleExpressionsEqual(sample, SampleExpression.fromJson(sample.toJson()));

        LOGGER.debug("\n{}", sample.toJson());

    }

    @Test
    void testMandatoryFields() {

        assertThrows(IllegalArgumentException.class, () -> new SampleExpression(null, "some label", "expression"));
        assertThrows(IllegalArgumentException.class, () -> new SampleExpression("", "some label", "expression"));
        assertThrows(IllegalArgumentException.class, () -> new SampleExpression("  ", "some label", "expression"));

        assertThrows(IllegalArgumentException.class, () -> new SampleExpression("id", null, "expression"));
        assertThrows(IllegalArgumentException.class, () -> new SampleExpression("id", "", "expression"));
        assertThrows(IllegalArgumentException.class, () -> new SampleExpression("id", "  ", "expression"));

        assertThrows(IllegalArgumentException.class, () -> new SampleExpression("id", "some", null));
        assertNotNull(new SampleExpression("id", "some", ""));

    }

    @Test
    void testSpecials() {

        SampleExpression sample1 = new SampleExpression("id", "label1", "foo");

        assertNull(sample1.group());

        SampleExpression sample2 = new SampleExpression("id", "other", "bar");

        assertEquals(sample1, sample2);

        Set<SampleExpression> set = new HashSet<>();

        set.add(sample1);

        assertEquals(1, set.size());

        set.remove(sample2);

        assertTrue(set.isEmpty());

        Object obj = sample1;

        assertNotEquals(obj, new Object());

        Object objNull = null;

        assertNotEquals(obj, objNull);

    }

    private SampleGenInfo createInfo() {

        SampleGenInfo info = new SampleGenInfo();

        info.setArgNames(Arrays.asList("a", "b"));
        info.setArgRefs(Arrays.asList("f", "g"));
        info.setArgValues(Arrays.asList("q"));
        info.setBoundValues(Arrays.asList(1, 2, 3));
        info.setCntAll(5);
        info.setCntAny(6);
        info.setCntBetween(0);
        info.setCntContains(0);
        info.setCntCurb(0);
        info.setCntIs(10);
        info.setCntNone(11);
        info.setCntNot(12);
        info.setCntStrict(13);
        info.setCntUnknown(14);
        info.setComments(Arrays.asList("c1", "c2"));
        info.setOperators(Arrays.asList(SampleExpressionOperator.values()));
        info.setSnippets(Arrays.asList("snippet1", "snippet3"));

        return info;
    }

    private void assertInfoExpectedInitially(SampleGenInfo info) {

        assertEquals(Arrays.asList("a", "b"), info.getArgNames());
        assertEquals(Arrays.asList("f", "g"), info.getArgRefs());
        assertEquals(Arrays.asList("q"), info.getArgValues());
        assertEquals(Arrays.asList(1, 2, 3), info.getBoundValues());
        assertEquals(5, info.getCntAll());
        assertEquals(6, info.getCntAny());
        assertEquals(0, info.getCntBetween());
        assertEquals(0, info.getCntContains());
        assertEquals(0, info.getCntCurb());
        assertEquals(10, info.getCntIs());
        assertEquals(11, info.getCntNone());
        assertEquals(12, info.getCntNot());
        assertEquals(13, info.getCntStrict());
        assertEquals(14, info.getCntUnknown());
        assertEquals(Arrays.asList("c1", "c2"), info.getComments());
        assertEquals(Arrays.asList(SampleExpressionOperator.values()), info.getOperators());
        assertEquals(Arrays.asList("snippet1", "snippet3"), info.getSnippets());

    }

    private void assertSampleExpressionsEqual(SampleExpression expected, SampleExpression actual) {

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.label(), actual.label());
        assertEquals(expected.expression(), actual.expression());
        assertEquals(expected.invalid(), actual.invalid());
        assertEquals(expected.composite(), actual.composite());

        assertTrue((expected.generationInfo() == null && actual.generationInfo() == null)
                || (expected.generationInfo() != null && actual.generationInfo() != null));

        if (expected.generationInfo() != null) {

            SampleGenInfo expectedInfo = expected.generationInfo();
            SampleGenInfo actualInfo = actual.generationInfo();

            assertEquals(expectedInfo.getArgNames(), actualInfo.getArgNames());
            assertEquals(expectedInfo.getArgRefs(), actualInfo.getArgRefs());
            assertEquals(expectedInfo.getArgValues(), actualInfo.getArgValues());
            assertEquals(expectedInfo.getBoundValues(), actualInfo.getBoundValues());
            assertEquals(expectedInfo.getCntAll(), actualInfo.getCntAll());
            assertEquals(expectedInfo.getCntAny(), actualInfo.getCntAny());
            assertEquals(expectedInfo.getCntBetween(), actualInfo.getCntBetween());
            assertEquals(expectedInfo.getCntContains(), actualInfo.getCntContains());
            assertEquals(expectedInfo.getCntCurb(), actualInfo.getCntCurb());
            assertEquals(expectedInfo.getCntIs(), actualInfo.getCntIs());
            assertEquals(expectedInfo.getCntNone(), actualInfo.getCntNone());
            assertEquals(expectedInfo.getCntNot(), actualInfo.getCntNot());
            assertEquals(expectedInfo.getCntStrict(), actualInfo.getCntStrict());
            assertEquals(expectedInfo.getCntUnknown(), actualInfo.getCntUnknown());
            assertEquals(expectedInfo.getComments(), actualInfo.getComments());
            assertEquals(expectedInfo.getOperators(), actualInfo.getOperators());
            assertEquals(expectedInfo.getSnippets(), actualInfo.getSnippets());

        }

    }

}
