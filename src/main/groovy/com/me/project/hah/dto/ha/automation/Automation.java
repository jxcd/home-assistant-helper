package com.me.project.hah.dto.ha.automation;

import java.util.List;

/**
 * @author cwj
 * @date 2022/8/29
 */
public record Automation(
        String name,
        String id,
        String description,

        ITrigger trigger,
        List<ICondition> conditionList,
        List<IAction> actionList
) {
}
