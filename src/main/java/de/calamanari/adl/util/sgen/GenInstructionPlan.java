//@formatter:off
/*
 * GenInstructionPlan
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plan with all groups of sample generation instructions to be executed in a single run.
 * <p/>
 * A plan is stable in the sense that repeated execution will produce the same output.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public record GenInstructionPlan(List<GenInstructionGroup> groups) {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenInstructionPlan.class);

    public GenInstructionPlan {
        if (groups == null) {
            throw new IllegalArgumentException("groups must not be null");
        }
        Map<String, String> idToGroupMap = new HashMap<>();

        for (GenInstructionGroup group : groups) {
            for (GenInstruction member : group.members()) {
                String otherGroup = idToGroupMap.putIfAbsent(member.template().id(), group.group());
                if (otherGroup != null) {
                    LOGGER.warn("SampleExpression ids should be unique across group templates, found id={} in group {} previously declared in group {}.",
                            member.template().id(), group.group(), otherGroup);
                }
            }
        }
    }

    /**
     * Prepares all included instructions with the supplier with the list of already generated samples before starting the actual generation process.
     * 
     * @param targetList list of already generated samples
     */
    public void prepare(List<SampleExpression> targetList) {
        groups.stream().forEach(group -> group.prepare(targetList));
    }

}
