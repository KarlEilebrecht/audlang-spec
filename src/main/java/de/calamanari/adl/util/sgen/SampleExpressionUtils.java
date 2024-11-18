//@formatter:off
/*
 * SampleExpressionUtils
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.calamanari.adl.util.JsonUtils;

/**
 * Set of utilities to deal with generating sample expressions and their persistence
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class SampleExpressionUtils {

    /**
     * Reads resource file with samples (must be a json array, even in case of a single sample)
     * 
     * @param fileName file name relative to the project root
     * @return list of samples
     * @throws IOException
     */
    public static List<SampleExpressionGroup> readSampleGroupsFromJsonResource(String fileName) throws IOException {

        try (InputStream is = SampleExpressionUtils.class.getResourceAsStream(fileName);
                BufferedInputStream bis = (is == null ? null : new BufferedInputStream(is))) {
            if (is == null) {
                throw new IOException("Unable to load sample resource " + fileName);
            }
            return Arrays.asList(JsonUtils.createObjectMapper(false).readValue(bis, SampleExpressionGroup[].class));
        }

    }

    /**
     * Reads any file with samples (must be a json array, even in case of a single sample)
     * 
     * @param fileName absolute file name
     * @return list of samples
     * @throws IOException
     */
    public static List<SampleExpressionGroup> readSampleGroupsFromJsonFile(String fileName) throws IOException {

        try (InputStream fis = new FileInputStream(fileName); BufferedInputStream bis = new BufferedInputStream(fis)) {
            return Arrays.asList(JsonUtils.createObjectMapper(false).readValue(bis, SampleExpressionGroup[].class));
        }

    }

    /**
     * Writes the given list of samples as a json file
     * 
     * @param samples to be written
     * @param fileName absolute file name
     * @throws IOException
     */
    public static void writeSampleGroupsToJsonFile(List<SampleExpressionGroup> samples, String fileName) throws IOException {

        try (OutputStream fos = new FileOutputStream(fileName); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            JsonUtils.createObjectMapper(true).writeValue(bos, samples);
        }

    }

    /**
     * Writes a catalog file listing group meta data
     * 
     * @param catalog to be written
     * @param fileName absolute file name
     * @throws IOException
     */
    public static void writeGroupCatalogToJsonFile(SampleGroupCatalog catalog, String fileName) throws IOException {
        try (OutputStream fos = new FileOutputStream(fileName); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            JsonUtils.createObjectMapper(true).writeValue(bos, catalog);
        }
    }

    /**
     * Creates an overview, useful with a plethora of generated samples
     * 
     * @param samples sample groups
     * @return catalog
     */
    public static SampleGroupCatalog createSampleGroupCatalog(List<SampleExpressionGroup> samples) {
        int numberOfSamples = samples.stream().map(group -> group.samples().size()).collect(Collectors.summingInt(Integer::intValue));
        int numberOfGroups = samples.size();
        List<SampleGroupCatalogEntry> groups = samples.stream().map(g -> new SampleGroupCatalogEntry(g.group(), g.samples().size())).toList();
        return new SampleGroupCatalog(groups, numberOfGroups, numberOfSamples);
    }

    /**
     * Generates {@link SampleExpression}s organized in {@link SampleExpressionGroup}s in memory.
     * 
     * @param sampleTemplateGroups templates for generating the samples
     * @return list of sample expression groups
     */
    public static List<SampleExpressionGroup> generateSamples(List<SampleExpressionGroup> sampleTemplateGroups) {

        InstructionPlanCreator planner = new InstructionPlanCreator();

        InstructionPlanExecutor executor = new InstructionPlanExecutor();

        return executor.execute(planner.createPlan(sampleTemplateGroups));

    }

    private SampleExpressionUtils() {
        // utilities
    }

}
