//@formatter:off
/*
 * InstructionPlanCreatorTest
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.calamanari.adl.AdlException;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class InstructionPlanCreatorTest {

    @Test
    void testBasics() {

        InstructionPlanCreator ipc = new InstructionPlanCreator();

        assertEquals(Collections.emptyList(), ipc.createPlan(null).groups());

        assertEquals(Collections.emptyList(), ipc.createPlan(new ArrayList<>()).groups());

        SampleExpression skippedExpression = new SampleExpression("skippedSample", "bla", "", true, false, true);

        SampleExpressionGroup groupWithSkippedSample = new SampleExpressionGroup("groupWithSkippedSample", Arrays.asList(skippedExpression));

        assertNotNull(ipc.createPlan(Arrays.asList(groupWithSkippedSample)));

        SampleExpressionGroup skippedGroup = new SampleExpressionGroup("groupWithSkippedSample", Arrays.asList(skippedExpression), true);

        assertNotNull(ipc.createPlan(Arrays.asList(skippedGroup)));

        SampleExpression someExpression = new SampleExpression("someSample", "bla", "some", true, false, false);

        List<SampleExpressionGroup> duplicateTemplateInGroup = Arrays
                .asList(new SampleExpressionGroup("groupWithDuplicateSample", Arrays.asList(someExpression, someExpression)));

        assertThrows(AdlException.class, () -> ipc.createPlan(duplicateTemplateInGroup));

        List<SampleExpressionGroup> duplicateTemplateInTwoGroup = Arrays.asList(
                new SampleExpressionGroup("groupWithDuplicateSample", Arrays.asList(someExpression)),
                new SampleExpressionGroup("groupWithDuplicateSample2", Arrays.asList(someExpression)));

        assertNotNull(ipc.createPlan(duplicateTemplateInTwoGroup));

        List<SampleExpressionGroup> duplicateGroup = Arrays.asList(new SampleExpressionGroup("group1", Arrays.asList(someExpression)),
                new SampleExpressionGroup("group1", Arrays.asList(someExpression)));

        assertThrows(AdlException.class, () -> ipc.createPlan(duplicateGroup));

    }

}
