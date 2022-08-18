package com.me.project.hah.dto.ha;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author cwj
 * @date 2022/8/18
 */
public record State(
        @JsonProperty("entity_id")
        String entityId,
        String state,
        @JsonProperty("last_changed")
        String lastChanged,
        Map<String, Object> attributes
) {
}
