//@formatter:off
/*
 * SampleExpression
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
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.calamanari.adl.util.JsonUtils;

/**
 * A {@link SampleExpression} can hold a template for an expression or a (generated) sample. Identity and thus equality is defined on its {@link #id()}.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 * 
 */
public record SampleExpression(String id, String label, String expression, @JsonInclude(JsonInclude.Include.NON_DEFAULT) boolean invalid,
        @JsonInclude(JsonInclude.Include.NON_DEFAULT) boolean composite, @JsonInclude(JsonInclude.Include.NON_DEFAULT) boolean skip,
        SampleGenInfo generationInfo) implements Serializable {

    /**
     * @param id mandatory unique identifier of the expression, the id also defines the expression's identity (equals, hashcode)
     * @param label mandatory human-readable description
     * @param expression the sample expression, may be empty but not null
     * @param invalid flag to indicate that {@link #expression()} is expected to be invalid
     * @param composite flag to indicate that the expression is composed (AND/OR) or other expression and thus may need braces when being concatenated
     * @param skip flag to disable a sample expression without removing it
     * @param generationInfo optional sample generation information, details might be used for validation purposes
     */
    public SampleExpression {

        if (id == null || label == null || expression == null) {
            throw new IllegalArgumentException(
                    String.format("id, label and expression must not be null, given: id=%s, label=%s, expression=%s", id, label, expression));
        }
        if (id.isBlank() || label.isBlank()) {
            throw new IllegalArgumentException(
                    String.format("id and label must not be empty or whitespace only, given: id='%s', label='%s', expression=%s", id, label, expression));
        }

    }

    /**
     * @param id mandatory unique identifier of the expression, the id also defines the expression's identity (equals, hashcode)
     * @param label mandatory human-readable description
     * @param expression the sample expression, may be empty but not null
     * @param invalid flag to indicate that {@link #expression()} is expected to be invalid
     * @param composite flag to indicate that the expression is composed (AND/OR) or other expression and thus may need braces when being concatenated
     * @param skip flag to disable a sample expression without removing it
     */
    public SampleExpression(String id, String label, String expression, boolean invalid, boolean composite, boolean skip) {
        this(id, label, expression, invalid, composite, skip, null);
    }

    /**
     * @param id mandatory unique identifier of the expression, the id also defines the expression's identity (equals, hashcode)
     * @param label mandatory human-readable description
     * @param expression the sample expression, may be empty but not null
     * @param invalid flag to indicate that {@link #expression()} is expected to be invalid
     * @param composite flag to indicate that the expression is composed (AND/OR) or other expression and thus may need braces when being concatenated
     */
    public SampleExpression(String id, String label, String expression, boolean invalid, boolean composite) {
        this(id, label, expression, invalid, composite, false, null);
    }

    /**
     * @param id mandatory unique identifier of the expression, the id also defines the expression's identity (equals, hashcode)
     * @param label mandatory human-readable description
     * @param expression the sample expression, may be empty but not null
     * @param invalid flag to indicate that {@link #expression()} is expected to be invalid
     */
    public SampleExpression(String id, String label, String expression, boolean invalid) {
        this(id, label, expression, invalid, false, false, null);
    }

    /**
     * @param id mandatory unique identifier of the expression, the id also defines the expression's identity (equals, hashcode)
     * @param label mandatory human-readable description
     * @param expression the sample expression, may be empty but not null
     */
    public SampleExpression(String id, String label, String expression) {
        this(id, label, expression, false, false, false, null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
        SampleExpression other = (SampleExpression) obj;
        return Objects.equals(id, other.id);
    }

    /**
     * Internal execution information, the group this expression belongs to during generation
     * 
     * @return group name (if present, otherwise null)
     */
    String group() {
        return this.generationInfo == null ? null : this.generationInfo.group;
    }

    /**
     * Read a single json-serialized expression
     * 
     * @param json source
     * @return sample expression instance
     */
    public static SampleExpression fromJson(String json) {
        return JsonUtils.readFromJsonString(json, SampleExpression.class);
    }

    /**
     * Writes this expression as properly formatted json string
     * 
     * @return json string
     */
    public String toJson() {
        return JsonUtils.writeAsJsonString(this, true);
    }

}
