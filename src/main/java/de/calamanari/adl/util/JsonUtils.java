//@formatter:off
/*
 * JsonUtil
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.calamanari.adl.AdlException;

/**
 * Common utilities for reading and writing json-files
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class JsonUtils {

    /**
     * Creates an object mapper with the following settings:
     * <ul>
     * <li>Ignore any unknown properties on de-serialization</li>
     * <li>Do not serialize properties with null-value.</li>
     * <li>Use snake-case (means underscore-separated words</li>
     * <li>Pretty-print (if flag true) with 4-space indentation and line-break preceding each array element.</li>
     * </ul>
     * 
     * @param prettyPrint if true create json with indentation, otherwise inline
     * @return object mapper
     */
    public static ObjectMapper createObjectMapper(boolean prettyPrint) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
        if (prettyPrint) {
            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
            prettyPrinter.indentObjectsWith(indenter);
            prettyPrinter.indentArraysWith(indenter);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setDefaultPrettyPrinter(prettyPrinter);
        }
        return objectMapper;
    }

    /**
     * Creates an object mapper with the following settings:
     * <ul>
     * <li>Ignore any unknown properties on de-serialization</li>
     * <li>Do not serialize properties with null-value.</li>
     * <li>Use snake-case (means underscore-separated words</li>
     * <li>inline json, no pretty-printing</li>
     * </ul>
     * 
     * @return object mapper
     */
    public static ObjectMapper createObjectMapper() {
        return createObjectMapper(false);
    }

    /**
     * Convenience method to write an java object as json string
     * 
     * @param value to be serialized
     * @param prettyPrint if true create json with indentation, otherwise inline
     * @return json
     */
    public static String writeAsJsonString(Object value, boolean prettyPrint) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot write NULL as json string.");
        }
        try {
            return createObjectMapper(prettyPrint).writeValueAsString(value);
        }
        catch (JsonProcessingException ex) {
            throw new AdlException(ex);
        }
    }

    /**
     * Convenience method to de-serialize a json string back into an object
     * 
     * @param <T> type of the expected object
     * @param json source text
     * @param type type class of the object to be created
     * @return object
     */
    public static <T> T readFromJsonString(String json, Class<T> type) {
        try {
            return createObjectMapper(false).readValue(json, type);
        }
        catch (JsonProcessingException | RuntimeException ex) {
            throw new AdlException(ex);
        }
    }

    private JsonUtils() {
        // no instances
    }
}
