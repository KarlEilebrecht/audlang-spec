//@formatter:off
/*
 * SampleExpressionOperator
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

/**
 * Set of common operators as enumeration
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public enum SampleExpressionOperator {

    EQUALS("="), NOT_EQUALS("!="), LESS_THAN("<"), GREATER_THAN(">"), LESS_THAN_OR_EQUALS("<="), GREATER_THAN_OR_EQUALS(">=");

    public final String token;

    SampleExpressionOperator(String token) {
        this.token = token;
    }

    public static SampleExpressionOperator resolve(String token) {
        switch (token) {
        case "=":
            return EQUALS;
        case "!=":
            return NOT_EQUALS;
        case "<":
            return LESS_THAN;
        case "<=":
            return LESS_THAN_OR_EQUALS;
        case ">":
            return GREATER_THAN;
        case ">=":
            return GREATER_THAN_OR_EQUALS;
        default:
            throw new IllegalArgumentException("Unsupported operator: '" + token + "'");
        }

    }
}
