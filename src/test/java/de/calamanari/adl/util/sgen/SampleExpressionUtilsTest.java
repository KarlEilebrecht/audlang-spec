//@formatter:off
/*
 * SampleExpressionUtilsTest
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class SampleExpressionUtilsTest {

    @Test
    void testReadSamplesFromJsonResource() throws IOException {

        List<SampleExpressionGroup> list = SampleExpressionUtils.readSampleGroupsFromJsonResource("/samples/sample-expressions-template.json");

        assertNotNull(list);

        assertFalse(list.isEmpty());

        assertEquals("basic-expressions", list.get(0).group());

    }

    @Test
    void testSpecials() throws IOException {

        assertThrows(IOException.class, () -> SampleExpressionUtils.readSampleGroupsFromJsonResource("hugo"));

        Path testFile = Files.createTempFile("test", ".json");

        String absFileName = testFile.toFile().getAbsolutePath();

        List<SampleExpressionGroup> sampleGroups = SampleExpressionUtils.readSampleGroupsFromJsonResource("/samples/sample-expressions-template.json");

        SampleExpressionUtils.writeSampleGroupsToJsonFile(sampleGroups, absFileName);

        List<SampleExpressionGroup> list = SampleExpressionUtils.readSampleGroupsFromJsonFile(absFileName);

        assertNotNull(list);

        assertEquals(sampleGroups.size(), list.size());

    }

}
