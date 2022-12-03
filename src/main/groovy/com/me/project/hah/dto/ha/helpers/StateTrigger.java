package com.me.project.hah.dto.ha.helpers;

import com.me.project.hah.dto.ha.automation.ITrigger;

import java.util.List;

/**
 * @author cwj
 * @date 2022/8/29
 */
public record StateTrigger(
        List<String> entityIdList
) implements ITrigger {

    public static StateTrigger of(String entityId) {
        return new StateTrigger(List.of(entityId));
    }
}
