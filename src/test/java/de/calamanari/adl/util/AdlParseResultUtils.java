//@formatter:off
/*
 * AdlParseResultUtils
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
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import de.calamanari.adl.util.sgen.SampleExpressionOperator;
import de.calamanari.adl.util.sgen.SampleGenInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * These utilities are related to testing the generated examples. The basic idea is to compare information from a parse run on a given sample expression agains
 * the generation information attached to the sample.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class AdlParseResultUtils {

    private static final Map<SampleExpressionOperator, String[]> OPERATOR_RULE_NAME_MAP;
    static {

        Map<SampleExpressionOperator, String[]> map = new EnumMap<>(SampleExpressionOperator.class);
        map.put(SampleExpressionOperator.EQUALS, new String[] { "cmpEquals", "curbEquals" });
        map.put(SampleExpressionOperator.NOT_EQUALS, new String[] { "cmpNotEquals", "curbNotEquals" });
        map.put(SampleExpressionOperator.GREATER_THAN, new String[] { "cmpGreaterThan", "curbGreaterThan" });
        map.put(SampleExpressionOperator.LESS_THAN, new String[] { "cmpLessThan", "curbLessThan" });
        map.put(SampleExpressionOperator.GREATER_THAN_OR_EQUALS, new String[] { "cmpGreaterThanOrEquals", "curbGreaterThanOrEquals" });
        map.put(SampleExpressionOperator.LESS_THAN_OR_EQUALS, new String[] { "cmpLessThanOrEquals", "curbLessThanOrEquals" });
        OPERATOR_RULE_NAME_MAP = Collections.unmodifiableMap(map);

    }

    private static SampleGenInfo convert(AntlrParseResult parseResult) {

        SampleGenInfo res = new SampleGenInfo();
        res.setCntAll(countRuleOccurrences(parseResult, "allExpression"));
        res.setCntAnd(countTokenOccurrences(parseResult, "AND"));
        res.setCntAny(countRuleOccurrences(parseResult, "cmpAnyOf", "cmpContainsAnyOf"));
        res.setCntBetween(countRuleOccurrences(parseResult, "cmpBetween"));
        res.setCntContains(countRuleOccurrences(parseResult, "cmpContainsAnyOf", "cmpContains"));
        res.setCntCurb(countRuleOccurrences(parseResult, "curbExpression"));
        res.setCntIs(countRuleOccurrences(parseResult, "cmpIsUnknown", "cmpIsNotUnknown"));
        res.setCntUnknown(countRuleOccurrences(parseResult, "cmpIsUnknown", "cmpIsNotUnknown"));
        res.setCntNone(countRuleOccurrences(parseResult, "noneExpression"));
        res.setCntNot(countRuleOccurrences(parseResult, "cmpIsNotUnknown", "cmpInnerNot", "notExpression"));
        res.setCntStrict(countRuleOccurrences(parseResult, "spaceAfterStrict"));
        res.setCntOf(countRuleOccurrences(parseResult, "cmpAnyOf", "cmpContainsAnyOf"));
        res.setCntOr(countTokenOccurrences(parseResult, "OR"));

        res.setArgNames(collectArgNames(parseResult));
        res.setArgValues(collectArgValues(parseResult));
        res.setArgRefs(collectArgRefs(parseResult));
        res.setBoundValues(collectBounds(parseResult));
        res.setComments(collectPlainValues(parseResult, "comment"));
        res.setOperators(collectOperators(parseResult));
        res.setSnippets(collectPlainValues(parseResult, "snippet"));

        return res;
    }

    public static void assertResultMatchesSampleGenInfo(SampleGenInfo expected, AntlrParseResult actualResult) {

        expected = SampleGenInfo.createEmptyInstanceNoNulls().combine(expected);
        SampleGenInfo actual = convert(actualResult);

        assertEquals(expected.getCntAll(), actual.getCntAll());
        assertEquals(expected.getCntAnd(), actual.getCntAnd());
        assertEquals(expected.getCntAny(), actual.getCntAny());
        assertEquals(expected.getCntBetween(), actual.getCntBetween());
        assertEquals(expected.getCntContains(), actual.getCntContains());
        assertEquals(expected.getCntCurb(), actual.getCntCurb());
        assertEquals(expected.getCntIs(), actual.getCntIs());
        assertEquals(expected.getCntUnknown(), actual.getCntUnknown());
        assertEquals(expected.getCntNone(), actual.getCntNone());
        assertEquals(expected.getCntNot(), actual.getCntNot());
        assertEquals(expected.getCntStrict(), actual.getCntStrict());
        assertEquals(expected.getCntOf(), actual.getCntOf());
        assertEquals(expected.getCntOr(), actual.getCntOr());

        assertSameElements(expected.getArgNames(), actual.getArgNames());
        assertSameElements(expected.getArgRefs(), actual.getArgRefs());
        assertSameElements(expected.getArgValues(), actual.getArgValues());
        assertSameElements(expected.getBoundValues(), actual.getBoundValues());
        assertSameElements(expected.getComments(), actual.getComments());
        assertSameElements(expected.getOperators(), actual.getOperators());
        assertSameElements(expected.getSnippets(), actual.getSnippets());
    }

    private static <T extends Comparable<T>> void assertSameElements(List<T> expected, List<T> actual) {

        List<T> expectedOrdered = new ArrayList<>(expected);
        Collections.sort(expectedOrdered);

        List<T> actualOrdered = new ArrayList<>(actual);
        Collections.sort(actualOrdered);

        assertEquals(expectedOrdered, actualOrdered);
    }

    private static List<String> collectPlainValues(AntlrParseResult parseResult, String ruleName) {

        List<String> res = new ArrayList<>();

        List<String> values = parseResult.ruleNameToValueMap.get(ruleName);
        if (values != null) {
            for (String value : values) {
                res.add(value);
            }
        }

        return res;
    }

    private static List<String> collectArgValues(AntlrParseResult parseResult) {

        List<String> res = new ArrayList<>(collectPlainValues(parseResult, "argValue").stream().toList());

        // snippets are technically argValues, so we need to subtract occurrences
        collectPlainValues(parseResult, "snippet").forEach(res::remove);

        return res;
    }

    private static List<String> collectArgNames(AntlrParseResult parseResult) {

        List<String> res = new ArrayList<>(collectPlainValues(parseResult, "argName").stream().toList());

        // argRefs are technically argNames, so we need to subtract occurrences
        collectArgRefs(parseResult).forEach(res::remove);

        return res;
    }

    private static List<String> collectArgRefs(AntlrParseResult parseResult) {
        return collectPlainValues(parseResult, "argRef").stream().map(s -> s.substring(1)).toList();
    }

    private static List<SampleExpressionOperator> collectOperators(AntlrParseResult parseResult) {

        List<SampleExpressionOperator> res = new ArrayList<>();

        for (Map.Entry<SampleExpressionOperator, String[]> entry : OPERATOR_RULE_NAME_MAP.entrySet()) {
            for (String ruleName : entry.getValue()) {
                List<String> values = parseResult.ruleNameToValueMap.get(ruleName);
                if (values != null) {
                    for (int i = 0; i < values.size(); i++) {
                        res.add(entry.getKey());
                    }
                }
            }
        }

        return res;
    }

    private static List<Integer> collectBounds(AntlrParseResult parseResult) {

        List<Integer> res = new ArrayList<>();

        List<String> textBounds = collectPlainValues(parseResult, "curbBound");
        for (String textBound : textBounds) {
            int val = -1;
            try {
                val = Integer.parseInt(textBound);
            }
            catch (NumberFormatException _) {
                // ignore
            }
            if (val >= 0) {
                res.add(val);
            }
        }

        return res;
    }

    private static int countRuleOccurrences(AntlrParseResult parseResult, String... ruleNames) {

        int res = 0;

        for (String ruleName : ruleNames) {

            List<String> values = parseResult.ruleNameToValueMap.get(ruleName);
            if (values != null) {
                res = res + (int) values.stream().count();
            }

        }
        return res;
    }

    private static int countTokenOccurrences(AntlrParseResult parseResult, String... tokenNames) {

        int res = 0;

        List<String> searchTokens = Arrays.asList(tokenNames);
        res = (int) parseResult.tokenList.stream().filter(searchTokens::contains).count();
        return res;
    }

    private AdlParseResultUtils() {
        // no instances
    }

}
