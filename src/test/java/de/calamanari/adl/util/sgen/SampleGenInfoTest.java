//@formatter:off
/*
 * SampleGenInfoTest
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class SampleGenInfoTest {

    @Test
    void testBasics() {

        SampleGenInfo info = new SampleGenInfo();

        info.setArgNames(new ArrayList<>(Arrays.asList("a", "b")));
        info.setArgRefs(new ArrayList<>(Arrays.asList("f", "g")));
        info.setArgValues(new ArrayList<>(Arrays.asList("q")));
        info.setBoundValues(new ArrayList<>(Arrays.asList(1, 2, 3)));
        info.setCntAll(5);
        info.setCntAny(6);
        info.setCntBetween(7);
        info.setCntContains(8);
        info.setCntCurb(9);
        info.setCntIs(10);
        info.setCntNone(11);
        info.setCntNot(12);
        info.setCntStrict(13);
        info.setCntUnknown(14);
        info.setComments(new ArrayList<>(Arrays.asList("c1", "c2")));
        info.setOperators(new ArrayList<>(Arrays.asList(SampleExpressionOperator.values())));
        info.setSnippets(new ArrayList<>(Arrays.asList("snippet1", "snippet3")));

        SampleGenInfo info2 = info.copy();

        assertInfoExpectedInitially(info2);

        info.incrementCntAll();
        info.incrementCntAny();
        info.incrementCntBetween();
        info.incrementCntContains();
        info.incrementCntCurb();
        info.incrementCntIs();
        info.incrementCntNone();
        info.incrementCntNot();
        info.incrementCntStrict();
        info.incrementCntUnknown();
        info.getArgNames().remove(1);
        info.getArgRefs().remove(1);
        info.getArgValues().remove(0);
        info.getBoundValues().remove(1);
        info.getComments().remove(0);
        info.getOperators().remove(0);
        info.getSnippets().remove(0);

        List<SampleExpressionOperator> sublist = new ArrayList<>(Arrays.asList(SampleExpressionOperator.values()));
        sublist.remove(0);

        assertEquals(Arrays.asList("a"), info.getArgNames());
        assertEquals(Arrays.asList("f"), info.getArgRefs());
        assertEquals(Collections.emptyList(), info.getArgValues());
        assertEquals(Arrays.asList(1, 3), info.getBoundValues());
        assertEquals(6, info.getCntAll());
        assertEquals(7, info.getCntAny());
        assertEquals(8, info.getCntBetween());
        assertEquals(9, info.getCntContains());
        assertEquals(10, info.getCntCurb());
        assertEquals(11, info.getCntIs());
        assertEquals(12, info.getCntNone());
        assertEquals(13, info.getCntNot());
        assertEquals(14, info.getCntStrict());
        assertEquals(15, info.getCntUnknown());
        assertEquals(Arrays.asList("c2"), info.getComments());
        assertEquals(sublist, info.getOperators());
        assertEquals(Arrays.asList("snippet3"), info.getSnippets());

        assertInfoExpectedInitially(info2);

        assertNotNull(info2.toString());

    }

    private void assertInfoExpectedInitially(SampleGenInfo info) {

        assertEquals(Arrays.asList("a", "b"), info.getArgNames());
        assertEquals(Arrays.asList("f", "g"), info.getArgRefs());
        assertEquals(Arrays.asList("q"), info.getArgValues());
        assertEquals(Arrays.asList(1, 2, 3), info.getBoundValues());
        assertEquals(5, info.getCntAll());
        assertEquals(6, info.getCntAny());
        assertEquals(7, info.getCntBetween());
        assertEquals(8, info.getCntContains());
        assertEquals(9, info.getCntCurb());
        assertEquals(10, info.getCntIs());
        assertEquals(11, info.getCntNone());
        assertEquals(12, info.getCntNot());
        assertEquals(13, info.getCntStrict());
        assertEquals(14, info.getCntUnknown());
        assertEquals(Arrays.asList("c1", "c2"), info.getComments());
        assertEquals(Arrays.asList(SampleExpressionOperator.values()), info.getOperators());
        assertEquals(Arrays.asList("snippet1", "snippet3"), info.getSnippets());

    }

}
