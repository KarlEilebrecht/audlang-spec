//@formatter:off
/*
 * InstructionPlanCreator
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.calamanari.adl.util.AdlException;

/**
 * Creates an execution plan based on a given list of template groups (ignoring any template group or template that is marked to be skipped)
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class InstructionPlanCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstructionPlanCreator.class);

    /**
     * converts the textual instructions from a script into instruction tokens that can be executed sequentially
     */
    private final TemplateInstructionParser templateInstructionParser = new TemplateInstructionParser();

    /**
     * Creates the execution plan from a list of template groups
     * 
     * @param templateGroups
     * @return execution plan
     */
    public GenInstructionPlan createPlan(List<SampleExpressionGroup> templateGroups) {

        if (templateGroups == null || templateGroups.isEmpty()) {
            return new GenInstructionPlan(Collections.emptyList());
        }

        Map<String, List<GenInstruction>> groupMap = new LinkedHashMap<>();

        for (SampleExpressionGroup templateGroup : templateGroups) {
            processTemplateGroup(templateGroup, groupMap);

        }

        List<GenInstructionGroup> generatorInstructionGroups = new ArrayList<>();

        for (Map.Entry<String, List<GenInstruction>> entry : groupMap.entrySet()) {

            if (entry.getValue().isEmpty()) {
                LOGGER.debug("Skipping empty group: '{}'", entry.getKey());
            }
            else {
                generatorInstructionGroups.add(new GenInstructionGroup(entry.getKey(), entry.getValue()));
            }

        }
        return new GenInstructionPlan(generatorInstructionGroups);

    }

    /**
     * @param templateGroup
     * @param groupMap
     */
    private void processTemplateGroup(SampleExpressionGroup templateGroup, Map<String, List<GenInstruction>> groupMap) {
        if (!templateGroup.skip()) {
            if (groupMap.containsKey(templateGroup.group())) {
                throw new AdlException(String.format("Found duplicate group name in instruction plan: %s, %s",
                        groupMap.keySet().stream().collect(Collectors.joining(", ")), templateGroup.group()));
            }
            List<GenInstruction> currentGroupMembers = new ArrayList<>();
            groupMap.put(templateGroup.group(), currentGroupMembers);

            Set<String> templateIdsInGroup = new LinkedHashSet<>();

            for (SampleExpression template : templateGroup.samples()) {
                processTemplate(template, templateGroup, templateIdsInGroup, currentGroupMembers);
            }
        }
        else {
            LOGGER.debug("Skipping template group: '{}'", templateGroup.group());
        }
    }

    /**
     * @param template
     * @param templateGroup
     * @param templateIdsInGroup
     * @param currentGroupMembers
     */
    private void processTemplate(SampleExpression template, SampleExpressionGroup templateGroup, Set<String> templateIdsInGroup,
            List<GenInstruction> currentGroupMembers) {
        if (!template.skip()) {
            if (templateIdsInGroup.contains(template.id())) {
                throw new AdlException(String.format("Found duplicate template id in template group %s: %s, %s", templateGroup.group(),
                        templateIdsInGroup.stream().collect(Collectors.joining(", ")), template.id()));

            }
            templateIdsInGroup.add(template.id());
            GenInstruction instruction = new GenInstruction(template, templateInstructionParser.parse(template.expression()));
            currentGroupMembers.add(instruction);
        }
        else {
            LOGGER.debug("Skipping template '{}' in group '{}'", template.id(), templateGroup.group());
        }
    }

}
