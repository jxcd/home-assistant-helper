package com.me.project.hah.dto.ha.helpers;

import com.me.project.hah.dto.ha.Services;
import com.me.project.hah.dto.ha.automation.IAction;

/**
 * @author cwj
 * @date 2022/8/29
 */
public record ServiceAction(
        String service,
        Object data
) implements IAction {

    public static ServiceAction of(String service, Object data) {
        return new ServiceAction(service, data);
    }

}
