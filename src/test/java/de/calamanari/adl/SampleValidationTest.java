//@formatter:off
/*
 * SampleValidationTest
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

package de.calamanari.adl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.calamanari.adl.antlr.AudlangLexer;
import de.calamanari.adl.antlr.AudlangParser;
import de.calamanari.adl.util.AdlParseResultUtils;
import de.calamanari.adl.util.AntlrParseResult;
import de.calamanari.adl.util.AntlrTestHelper;
import de.calamanari.adl.util.JsonUtils;
import de.calamanari.adl.util.sgen.SampleExpression;
import de.calamanari.adl.util.sgen.SampleExpressionGroup;
import de.calamanari.adl.util.sgen.SampleExpressionUtils;
import de.calamanari.adl.util.sgen.SampleGroupCatalog;

/**
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
class SampleValidationTest {

    static final Logger LOGGER = LoggerFactory.getLogger(SampleValidationTest.class);

    private AntlrTestHelper testHelper = new AntlrTestHelper(AudlangLexer.class, AudlangParser.class, "query");

    @Test
    @Disabled("for debugging")
    void testSampleParsingWithHelper() {

        AntlrParseResult res = testHelper.parse("c contains a@s");

        assertNotNull(res);
        LOGGER.info("\n{}", res);
    }

    @Test
    void testPersistSamples() throws IOException {

        // Note: this test generates the samples to be included in the project's zip

        String outputDirectory = "target/generated-samples/";

        List<SampleExpressionGroup> allGroups = generateSamples();

        assertNotNull(allGroups);
        assertFalse(allGroups.isEmpty());

        LOGGER.debug("Writing group files ...");

        Files.createDirectories(Paths.get(outputDirectory));

        LOGGER.debug("Output directory: {}", Paths.get(outputDirectory).toAbsolutePath());

        for (SampleExpressionGroup sampleGroup : allGroups) {

            String groupFileName = sampleGroup.group() + ".json";

            LOGGER.debug("Creating group file {}", groupFileName);
            SampleExpressionUtils.writeSampleGroupsToJsonFile(Arrays.asList(sampleGroup), outputDirectory + groupFileName);

        }

        String catalogFileName = "sample-group-catalog.json";

        LOGGER.debug("Creating catalog {}", catalogFileName);
        SampleGroupCatalog catalog = SampleExpressionUtils.createSampleGroupCatalog(allGroups);
        SampleExpressionUtils.writeGroupCatalogToJsonFile(catalog, outputDirectory + catalogFileName);

        LOGGER.debug("Overview:\n{}", JsonUtils.writeAsJsonString(catalog, false));
    }

    @Test
    void testGeneratedSamples() {

        List<SampleExpressionGroup> allGroups = generateSamples();

        assertNotNull(allGroups);
        assertFalse(allGroups.isEmpty());

        LOGGER.info("Testing {} sample groups ...", allGroups.size());

        int sampleCount = 0;
        int sampleGroupCount = 0;

        for (SampleExpressionGroup sampleGroup : allGroups) {

            LOGGER.debug("Testing sample group {} ...", sampleGroup.group());

            if (sampleGroup.skip()) {
                LOGGER.debug("Skipping group {}", sampleGroup.group());
            }
            else {
                sampleGroupCount++;
                List<SampleExpression> activeSamples = sampleGroup.samples().stream().filter(Predicate.not(sample -> sample.skip())).toList();

                LOGGER.debug("Found {} active samples ...", activeSamples.size());

                sampleCount = sampleCount + evaluateSamples(sampleGroup.group(), activeSamples, true);
                sampleCount = sampleCount + evaluateSamples(sampleGroup.group(), activeSamples, false);

                LOGGER.debug("Finished group {}.", sampleGroup.group());

            }

        }

        LOGGER.info("Successfully tested {}/{} samples in {}/{} sample groups.", sampleCount,
                allGroups.stream().map(g -> g.samples().size()).collect(Collectors.summingInt(Integer::intValue)), sampleGroupCount, allGroups.size());

    }

    private int evaluateSamples(String group, List<SampleExpression> allSamples, boolean filterValid) {
        List<SampleExpression> samples = allSamples.stream().filter(sample -> sample.invalid() != filterValid).toList();

        if (!samples.isEmpty()) {
            LOGGER.debug("Testing {} samples from group {} expected to be {}valid ...", samples.size(), group, (filterValid ? "" : "in"));
            samples.forEach(this::evaluateSample);
        }
        else {
            LOGGER.debug("No {}valid samples to test.", (filterValid ? "" : "in"));
        }
        return samples.size();
    }

    private void evaluateSample(SampleExpression sample) {

        LOGGER.debug("{}", sample.id());
        AntlrParseResult res = testHelper.parse(sample.expression());

        if (sample.invalid() != res.isError()) {

            LOGGER.error("ERROR: Sample {} was expected to be {}valid but was found {}valid", sample.id(), (sample.invalid() ? "in" : ""),
                    (res.isError() ? "in" : ""));
            LOGGER.error("Expression was: '''{}''', \nParse Result:\n{}", sample.expression(), res);

        }
        assertEquals(sample.invalid(), res.isError());

        if (!sample.invalid() && sample.generationInfo() != null) {
            boolean success = false;
            try {
                AdlParseResultUtils.assertResultMatchesSampleGenInfo(sample.generationInfo(), res);
                success = true;
            }
            finally {
                if (!success) {
                    LOGGER.error("ERROR: Sample {} was valid but parse result did not match expectations", sample.id());
                    LOGGER.error("Expression was: '''{}''', \nParse Result:\n{}", sample.expression(), res);
                }
            }
        }
    }

    private List<SampleExpressionGroup> generateSamples() {

        String templateFileName = "/samples/sample-expressions-template.json";

        try {

            List<SampleExpressionGroup> templateGroups = SampleExpressionUtils.readSampleGroupsFromJsonResource(templateFileName);

            LOGGER.info("Creating samples from template: {}", templateFileName);

            List<SampleExpressionGroup> sampleGroups = SampleExpressionUtils.generateSamples(templateGroups);

            int numberOfSamples = sampleGroups.stream().map(group -> group.samples().size()).collect(Collectors.summingInt(Integer::intValue));

            LOGGER.info("Generated {} samples within {} groups.", numberOfSamples, sampleGroups.size());

            return Collections.unmodifiableList(sampleGroups);
        }
        catch (IOException ex) {
            throw new RuntimeException("Could not load template file: " + templateFileName);
        }
    }

}
