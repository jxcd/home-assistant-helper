package com.me.project.hah.service.impl;

import com.me.project.hah.dto.ha.State;
import com.me.project.hah.dto.ha.StateFull;
import com.me.project.hah.service.HaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class HaServiceImplTest {
    private static final Logger log = LoggerFactory.getLogger(HaServiceImplTest.class);

    @Autowired
    private HaService haService;

    @Test
    public void test_get() {
        var states = haService.states();
        log.info("find {} states", states.size());
        for (var it : states) {
            log.info("{}", it);
        }

        var services = haService.services();
        log.info("find {} services", services.size());
        for (var it : services) {
            log.info("{}", it);
        }
    }

    @Test
    public void test_get_one_state() {
        // State[entityId=input_text.xiaoai_tts, state=你好啊, lastChanged=2022-08-17T02:38:14.133944+00:00, attributes={editable=false, min=0, max=100, pattern=null, mode=text, icon=mdi:comment-text, friendly_name=小爱tts}]
        String entityId = "input_text.xiaoai_tts";
        State state = haService.state(entityId);
        log.info("{}", state);

        State ns = new State(entityId, "测试一下rest接口", null, null);
        haService.updateState(ns);

        state = haService.state(entityId);
        log.info("{}", state);
    }

    @Test
    public void test_create_state() {
        String entityId = "input_text.xiaoai_tts_2";
        var ns = new StateFull(entityId, "测试一下rest接口", null, Map.of("hello", "创建成功"), entityId, "小爱tts2");
        haService.createState(ns);

        var state = haService.state(entityId);
        log.info("{}", state);
    }

    @Test
    public void test_call_service() {
        haService.callService("xiaomi_miot", "intelligent_speaker", Map.of(
                "entity_id", "media_player.xiaomi_xxx_play_control",
                "text", "你好啊, 这里是 media_player.xiaomi_xxx_play_control"
        )).forEach(state -> log.info("result: {}", state));

        haService.callService("xiaomi_miot", "intelligent_speaker", Map.of(
                "entity_id", "media_player.xiaomi_xxx_play_control",
                "text", "报时",
                "execute", "true"
        )).forEach(state -> log.info("result: {}", state));
    }

}