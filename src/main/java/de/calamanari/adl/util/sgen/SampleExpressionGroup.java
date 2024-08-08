//@formatter:off
/*
 * SampleExpressionGroup
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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.calamanari.adl.util.JsonUtils;

/**
 * A {@link SampleExpressionGroup} keeps together samples of a certain kind with an associated id.
 * <p/>
 * Note: The identifier {@link #group()} defines the identity of the group (equals/hashCode) and also potentially serves as a file name to store a sample group.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record SampleExpressionGroup(String group, List<SampleExpression> samples, @JsonInclude(JsonInclude.Include.NON_DEFAULT) boolean skip)
        implements Serializable {

    private static SampleExpression updateSampleWithGroup(SampleExpression sample, String group) {
        SampleExpression res = sample;
        if (sample.generationInfo() == null) {
            res = new SampleExpression(sample.id(), sample.label(), sample.expression(), sample.invalid(), sample.composite(), sample.skip(),
                    new SampleGenInfo());
            res.generationInfo().group = group;
        }
        else if (!group.equals(sample.generationInfo().group)) {
            res = new SampleExpression(sample.id(), sample.label(), sample.expression(), sample.invalid(), sample.composite(), sample.skip(),
                    sample.generationInfo().copy());
            res.generationInfo().group = group;
        }
        return res;
    }

    /**
     * @param group mandatory unique group identifier (identity of a group for equals, hashcode)
     * @param samples mandatory non-empty list of samples
     * @param skip to disable this group without removing it
     */
    public SampleExpressionGroup {

        if (group == null || samples == null) {
            throw new IllegalArgumentException(String.format("group identifier and samples must not be null, given: group=%s, samples=%s", group,
                    (samples == null ? null : samples.size())));
        }

        if (group.isBlank()) {
            throw new IllegalArgumentException(
                    String.format("group identifier must not be empty or whitespace-only, given: group='%s', samples=%s", group, "[" + samples.size() + "]"));
        }

        if (samples.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("A group of samples must at least contain one element, given: group='%s', samples=%s", group, "[" + samples.size() + "]"));

        }
        samples = samples.stream().map(sample -> updateSampleWithGroup(sample, group)).toList();

    }

    /**
     * @param group mandatory unique group identifier (identity of a group for equals, hashcode)
     * @param samples mandatory non-empty list of samples
     */
    public SampleExpressionGroup(String group, List<SampleExpression> samples) {
        this(group, samples, false);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SampleExpressionGroup other = (SampleExpressionGroup) obj;
        return Objects.equals(group, other.group);
    }

    /**
     * Read a single json-serialized expression group
     * 
     * @param json source
     * @return sample expression instance
     */
    public static SampleExpressionGroup fromJson(String json) {
        return JsonUtils.readFromJsonString(json, SampleExpressionGroup.class);
    }

    /**
     * Writes this expression group as properly formatted json string
     * 
     * @return json string
     */
    public String toJson() {
        return JsonUtils.writeAsJsonString(this, true);
    }

}
