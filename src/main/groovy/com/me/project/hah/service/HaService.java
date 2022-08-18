package com.me.project.hah.service;

import com.me.project.hah.dto.ha.Services;
import com.me.project.hah.dto.ha.State;
import com.me.project.hah.dto.ha.StateFull;

import java.util.List;
import java.util.Map;

/**
 * <a href="https://developers.home-assistant.io/docs/api/rest/">rest</a>
 * <a href="https://developers.home-assistant.io/docs/api/websocket#message-format">websocket</a>
 *
 * @author cwj
 * @date 2022/8/18
 */
public interface HaService {

    /**
     * GET /api/states
     * Returns an array of state objects
     *
     * @return 所有实体
     */
    List<State> states();

    /**
     * GET /api/states/<entity_id>
     * Returns a state object for specified entity_id
     *
     * @param entityId 实体id
     * @return 实体
     */
    State state(String entityId);

    /**
     * POST /api/states/<entity_id>
     * Updates a state.
     *
     * @param state 实体
     */
    void updateState(State state);

    /**
     * POST /api/states/<entity_id>
     * Creates a state.
     *
     * @param state 实体
     */
    void createState(StateFull state);

    /**
     * GET /api/services
     * Returns an array of service objects
     *
     * @return 服务集合
     */
    List<Services> services();

    /**
     * POST /api/services/<domain>/<service>
     * Returns an array of service objects
     * <p>
     * same as 1:
     * url:     /api/services/switch/turn_on
     * data:    {"entity_id": "switch.christmas_lights"}
     * <p>
     * same as 2:
     * url:     /api/services/mqtt/publish
     * data:    {"payload": "OFF", "topic": "home/fridge", "retain": "True"}
     *
     * @return 服务集合
     */
    List<State> callService(String domain, String item, Map<String, Object> data);
}
