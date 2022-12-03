package com.me.project.hah.dto.ha.helpers;

import com.me.project.hah.dto.ha.automation.Automation;

/**
 * @author cwj
 * @date 2022/8/29
 */
public record InputButton(
        String name,
        String id,
        String icon,
        Automation automation
) {

    public static InputButton of(String name, String id, String icon, Automation automation) {
        return new InputButton(name, id, icon, automation);
    }

}
