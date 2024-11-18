//@formatter:off
/*
 * SampleGenInfo
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.calamanari.adl.util.JsonUtils;

/**
 * A {@link SampleGenInfo} stores details about a sample's generation process (number of tokens, values, comments, etc.).
 * <p>
 * This information can later be used to test whether the corresponding ANTLR-parser understands the generated example correctly.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class SampleGenInfo implements Serializable {

    private static final long serialVersionUID = 2428148984810966890L;

    /**
     * Internal processing information (awareness of the group during generation)
     */
    @JsonIgnore
    String group = null;

    /**
     * count of generated ALL-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntAll;

    /**
     * count of generated NONE-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntNone;

    /**
     * count of generated IS-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntIs;

    /**
     * count of generated NOT-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntNot;

    /**
     * count of generated UNKNOWN-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntUnknown;

    /**
     * count of generated STRICT-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntStrict;

    /**
     * count of generated CONTAINS-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntContains;

    /**
     * count of generated ANY-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntAny;

    /**
     * count of generated BETWEEN-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntBetween;

    /**
     * count of generated CURB-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntCurb;

    /**
     * count of generated AND-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntAnd;

    /**
     * count of generated OR-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntOr;

    /**
     * count of generated OF-tokens
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int cntOf;

    /**
     * list of generated bound-values
     */
    private List<Integer> boundValues;

    /**
     * list of generated operator-values
     */
    private List<SampleExpressionOperator> operators;

    /**
     * list of generated argument names
     */
    private List<String> argNames;

    /**
     * list of generated argument or list values
     */
    private List<String> argValues;

    /**
     * list of generated argument references
     */
    private List<String> argRefs;

    /**
     * list of generated text snippets
     */
    private List<String> snippets;

    /**
     * list of generated comments
     */
    private List<String> comments;

    public SampleGenInfo() {
        // default constructor
    }

    SampleGenInfo(String group) {
        this.group = group;
    }

    /**
     * @return count of generated ALL-tokens
     */
    public int getCntAll() {
        return cntAll;
    }

    /**
     * @param cntAll count of generated ALL-tokens
     */
    public void setCntAll(int cntAll) {
        this.cntAll = cntAll;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntAll() {
        this.cntAll++;
    }

    /**
     * @return count of generated NONE-tokens
     */
    public int getCntNone() {
        return cntNone;
    }

    /**
     * @param cntNone count of generated NONE-tokens
     */
    public void setCntNone(int cntNone) {
        this.cntNone = cntNone;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntNone() {
        this.cntNone++;
    }

    /**
     * @return count of generated IS-tokens
     */
    public int getCntIs() {
        return cntIs;
    }

    /**
     * @param cntIs count of generated IS-tokens
     */
    public void setCntIs(int cntIs) {
        this.cntIs = cntIs;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntIs() {
        this.cntIs++;
    }

    /**
     * @return count of generated NOT-tokens
     */
    public int getCntNot() {
        return cntNot;
    }

    /**
     * @param cntNot count of generated NOT-tokens
     */
    public void setCntNot(int cntNot) {
        this.cntNot = cntNot;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntNot() {
        this.cntNot++;
    }

    /**
     * @return count of generated UNKNOWN-tokens
     */
    public int getCntUnknown() {
        return cntUnknown;
    }

    /**
     * @param cntUnknown count of generated UNKNOWN-tokens
     */
    public void setCntUnknown(int cntUnknown) {
        this.cntUnknown = cntUnknown;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntUnknown() {
        this.cntUnknown++;
    }

    /**
     * @return count of generated STRICT-tokens
     */
    public int getCntStrict() {
        return cntStrict;
    }

    /**
     * @param cntStrict count of generated STRICT-tokens
     */
    public void setCntStrict(int cntStrict) {
        this.cntStrict = cntStrict;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntStrict() {
        this.cntStrict++;
    }

    /**
     * @return count of generated CONTAINS-tokens
     */
    public int getCntContains() {
        return cntContains;
    }

    /**
     * @param cntContains count of generated CONTAINS-tokens
     */
    public void setCntContains(int cntContains) {
        this.cntContains = cntContains;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntContains() {
        this.cntContains++;
    }

    /**
     * @return count of generated ANY-tokens
     */
    public int getCntAny() {
        return cntAny;
    }

    /**
     * @param cntAny count of generated ANY-tokens
     */
    public void setCntAny(int cntAny) {
        this.cntAny = cntAny;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntAny() {
        this.cntAny++;
    }

    /**
     * @return count of generated BETWEEN-tokens
     */
    public int getCntBetween() {
        return cntBetween;
    }

    /**
     * @param cntBetween count of generated BETWEEN-tokens
     */
    public void setCntBetween(int cntBetween) {
        this.cntBetween = cntBetween;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntBetween() {
        this.cntBetween++;
    }

    /**
     * @return count of generated CURB-tokens
     */
    public int getCntCurb() {
        return cntCurb;
    }

    /**
     * @param cntCurb count of generated CURB-tokens
     */
    public void setCntCurb(int cntCurb) {
        this.cntCurb = cntCurb;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntCurb() {
        this.cntCurb++;
    }

    /**
     * @return count of generated AND-tokens
     */
    public int getCntAnd() {
        return cntAnd;
    }

    /**
     * @param cntAnd count of generated AND-tokens
     */
    public void setCntAnd(int cntAnd) {
        this.cntAnd = cntAnd;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntAnd() {
        this.cntAnd++;
    }

    /**
     * @return count of generated OR-tokens
     */
    public int getCntOr() {
        return cntOr;
    }

    /**
     * @param cntOr count of generated OR-tokens
     */
    public void setCntOr(int cntOr) {
        this.cntOr = cntOr;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntOr() {
        this.cntOr++;
    }

    /**
     * @return count of generated OR-tokens
     */
    public int getCntOf() {
        return cntOf;
    }

    /**
     * @param cntOf count of generated OR-tokens
     */
    public void setCntOf(int cntOf) {
        this.cntOf = cntOf;
    }

    /**
     * shorthand for get, increment set
     */
    public void incrementCntOf() {
        this.cntOf++;
    }

    /**
     * @return list of generated bound values
     */
    public List<Integer> getBoundValues() {
        return boundValues;
    }

    /**
     * @param boundValues list of generated bound values
     */
    public void setBoundValues(List<Integer> boundValues) {
        this.boundValues = boundValues;
    }

    /**
     * @return list of generated operators
     */
    public List<SampleExpressionOperator> getOperators() {
        return operators;
    }

    /**
     * @param operators list of generated operators
     */
    public void setOperators(List<SampleExpressionOperator> operators) {
        this.operators = operators;
    }

    /**
     * @return list of generated argument names
     */
    public List<String> getArgNames() {
        return argNames;
    }

    /**
     * @param argNames list of generated argument names
     */
    public void setArgNames(List<String> argNames) {
        this.argNames = argNames;
    }

    /**
     * @return list of generated argument values
     */
    public List<String> getArgValues() {
        return argValues;
    }

    /**
     * @param argValues list of generated argument values
     */
    public void setArgValues(List<String> argValues) {
        this.argValues = argValues;
    }

    /**
     * @return list of generated argument name references
     */
    public List<String> getArgRefs() {
        return argRefs;
    }

    /**
     * @param argRefs list of generated argument name references
     */
    public void setArgRefs(List<String> argRefs) {
        this.argRefs = argRefs;
    }

    /**
     * @return list of generated text snippets
     */
    public List<String> getSnippets() {
        return snippets;
    }

    /**
     * @param snippets list of generated text snippets
     */
    public void setSnippets(List<String> snippets) {
        this.snippets = snippets;
    }

    /**
     * @return list of generated comments
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * @param comments list of generated comments
     */
    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    /**
     * Creates an <i>independent</i> deep-copy of this instance.
     * 
     * @return copy info
     */
    public SampleGenInfo copy() {
        SampleGenInfo res = JsonUtils.readFromJsonString(JsonUtils.writeAsJsonString(this, false), SampleGenInfo.class);
        res.group = this.group;
        return res;
    }

    /**
     * Creates a new instance with the combined information (the given info <i>added</i> to this info)
     * 
     * @param other other information object
     * @return sum info
     */
    public SampleGenInfo combine(SampleGenInfo other) {
        SampleGenInfo res = this.copy();

        combineLists(res.argNames, other.argNames, res::setArgNames);
        combineLists(res.argRefs, other.argRefs, res::setArgRefs);
        combineLists(res.argValues, other.argValues, res::setArgValues);
        combineLists(res.boundValues, other.boundValues, res::setBoundValues);
        combineLists(res.comments, other.comments, res::setComments);
        combineLists(res.operators, other.operators, res::setOperators);
        combineLists(res.snippets, other.snippets, res::setSnippets);

        res.cntAll = res.cntAll + other.cntAll;
        res.cntAnd = res.cntAnd + other.cntAnd;
        res.cntAny = res.cntAny + other.cntAny;
        res.cntBetween = res.cntBetween + other.cntBetween;
        res.cntContains = res.cntContains + other.cntContains;
        res.cntCurb = res.cntCurb + other.cntCurb;
        res.cntIs = res.cntIs + other.cntIs;
        res.cntNone = res.cntNone + other.cntNone;
        res.cntNot = res.cntNot + other.cntNot;
        res.cntOf = res.cntOf + other.cntOf;
        res.cntOr = res.cntOr + other.cntOr;
        res.cntStrict = res.cntStrict + other.cntStrict;
        res.cntUnknown = res.cntUnknown + other.cntUnknown;
        return res;
    }

    private <T> void combineLists(List<T> left, List<T> right, Consumer<List<T>> leftSetter) {

        if (left != null && right != null) {
            left.addAll(right);
        }
        else if (left == null && right != null) {
            leftSetter.accept(new ArrayList<>(right));
        }

    }

    public static SampleGenInfo createEmptyInstanceNoNulls() {

        SampleGenInfo info = new SampleGenInfo();
        info.setArgNames(new ArrayList<>());
        info.setArgRefs(new ArrayList<>());
        info.setArgValues(new ArrayList<>());
        info.setBoundValues(new ArrayList<>());
        info.setComments(new ArrayList<>());
        info.setOperators(new ArrayList<>());
        info.setSnippets(new ArrayList<>());

        return info;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [cntAll=" + cntAll + ", cntNone=" + cntNone + ", cntIs=" + cntIs + ", cntNot=" + cntNot + ", cntUnknown="
                + cntUnknown + ", cntStrict=" + cntStrict + ", cntContains=" + cntContains + ", cntAny=" + cntAny + ", cntOf=\" + cntOf + \", cntBetween="
                + cntBetween + ", cntCurb=" + cntCurb + ", cntAnd=" + cntAnd + ", cntOr=" + cntOr + ", boundValues=" + boundValues + ", operators=" + operators
                + ", argNames=" + argNames + ", argValues=" + argValues + ", argRefs=" + argRefs + ", snippets=" + snippets + ", comments=" + comments + "]";
    }

}
