package com.me.project.hah.dto.ha;

/**
 * Each object contains the domain and which services it contains.
 *
 * @author cwj
 * @date 2022/8/18
 */
public record Services(
        String domain,
        Object services
) {
}
