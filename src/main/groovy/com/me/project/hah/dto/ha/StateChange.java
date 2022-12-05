package com.me.project.hah.dto.ha;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cwj
 * @date 2022/12/6
 */
public record StateChange(
        @JsonProperty("entity_id")
        String entityId,
        @JsonProperty("new_state")
        State newState,
        @JsonProperty("old_state")
        State oldState
) {
}
