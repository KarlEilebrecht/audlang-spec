//@formatter:off
/*
 * IdFilterReuseExpressionInstruction
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

import java.util.List;
import java.util.function.Predicate;

/**
 * An {@link IdFilterReuseExpressionInstruction} appends the text of one or multiple referenced previously generated sample expressions to the expression
 * generated until this point.
 * <p/>
 * The logic based on a given idFilter text works as follows:
 * <ul>
 * <li>First check if there is an exact match among the expression ids generated so far, in case of multiple, take the first occurrence to keep the process
 * predictable and potentially <i>nicer</i> as subsequent variations are usually uglier versions of a certain template.</li>
 * <li>If no expression matches, check if there is a group with a matching name. In this case pick group members randomly.</li>
 * </ul>
 * This allows including previously generated expressions in subsequently generated expressions either by id or group name.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class IdFilterReuseExpressionInstruction extends AbstractReuseExpressionInstruction {

    /**
     * template id or group name
     */
    protected final String idFilter;

    /**
     * @param filter template id or group name
     */
    public IdFilterReuseExpressionInstruction(String idFilter) {
        super(baseList -> filter(baseList, idFilter));
        this.idFilter = idFilter;
    }

    /**
     * Filter to be executed at generation time (not at construction time!).
     * 
     * @param baseList all previously generated expressions
     * @param idFilter template id or group name
     * @return all valid expressions matching the filter condition
     */
    private static List<SampleExpression> filter(List<SampleExpression> baseList, String idFilter) {

        // match only the first occurrence to make the result more predictable
        List<SampleExpression> res = baseList.stream().filter(Predicate.not(SampleExpression::invalid)).filter(e -> matchId(e, idFilter)).limit(1).toList();

        if (res.isEmpty()) {
            // try with group name
            res = baseList.stream().filter(Predicate.not(SampleExpression::invalid)).filter(e -> idFilter.equals(e.group())).toList();

        }
        return res;

    }

    /**
     * At runtime every expression gets a unique hash-id-suffix, this method strips the suffix when searching by id.
     * 
     * @param expression candidate
     * @param idFilter
     * @return true if id matches filter
     */
    private static boolean matchId(SampleExpression expression, String idFilter) {
        boolean res = false;
        String id = expression.id();
        int pos = id.lastIndexOf('_');
        if (pos > 0 && id.substring(0, pos).equals(idFilter)) {
            res = true;
        }
        return res;
    }

    @Override
    public String toString() {
        return "EXPRESSION:" + idFilter;
    }

}
