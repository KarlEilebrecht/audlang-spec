//@formatter:off
/*
 * ParseResult
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@link AntlrParseResult} contains the collected information from a single parse run from ANTLR.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class AntlrParseResult {

    /**
     * List with all tokens (names like in the ANTLR grammar)
     */
    public List<String> tokenList = new ArrayList<>();

    /**
     * Map with rule names to parsed text related to that rule
     */
    public Map<String, List<String>> ruleNameToValueMap = new LinkedHashMap<>();

    /**
     * Error if there was any
     */
    public Exception error = null;

    /**
     * @return true if the expression could not be parsed
     */
    public boolean isError() {
        return error != null;
    }

    @Override
    public String toString() {
        return "AntlrParseResult [\n    ruleNameToValueMap=" + formatRuleNameToValueMap() + ",\n    token=["
                + tokenList.stream().collect(Collectors.joining(", ")) + "],\n    error=" + error + "\n]";
    }

    private String formatRuleNameToValueMap() {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : ruleNameToValueMap.entrySet()) {

            sb.append("\n        ");
            sb.append(entry.getKey());
            sb.append("=[\n            ");
            sb.append(entry.getValue().stream().collect(Collectors.joining(",\n            ")));
            sb.append("\n        ]");

        }
        return sb.toString();
    }

}
