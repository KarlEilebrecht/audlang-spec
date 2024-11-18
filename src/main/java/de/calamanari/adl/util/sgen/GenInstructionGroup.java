//@formatter:off
/*
 * GenInstructionGroup
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

/**
 * Keeps together all generation instructions of a group
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record GenInstructionGroup(String group, List<GenInstruction> members) {

    /**
     * @param group
     * @param members
     */
    public GenInstructionGroup {
        if (group == null || group.isBlank() || members == null) {
            throw new IllegalArgumentException(String.format("Arguments group and members must not be null or blank, given: group=%s, members=%s", group,
                    (members == null) ? null : "[" + members.size() + "]"));
        }

    }

    /**
     * prepares all group members with the supplier with the list of already generated samples before starting generation
     * 
     * @param targetList the list of already generated samples
     */
    public void prepare(List<SampleExpression> targetList) {
        members.stream().forEach(instruction -> instruction.prepare(targetList));
    }

}
