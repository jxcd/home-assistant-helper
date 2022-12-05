package com.me.project.hah.service.impl

import com.me.project.hah.dto.conf.HaConf
import com.me.project.hah.dto.ha.State
import com.me.project.hah.dto.ha.StateChange
import com.me.project.hah.service.CacheService
import com.me.project.hah.service.HaSubscribeProcessService
import com.me.project.hah.service.WsMessageService
import com.me.project.hah.util.JsonUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.websocket.Session
import java.util.concurrent.ConcurrentHashMap

@Service
class WsMessageServiceImpl implements WsMessageService {
    private static final Logger log = LoggerFactory.getLogger(WsMessageServiceImpl.class);

    @Autowired
    HaConf haConf
    @Autowired
    CacheService cacheService
    @Autowired
    HaSubscribeProcessService subscribeProcessService

    /**
     * 内部处理器在这里添加
     */
    Map<String, Closure> innerConsumer = new ConcurrentHashMap<>()

    @Override
    void onMessage(String message, Session session) {
        log.debug("from session-{} rec: {}", session.getId(), message)

        def result = JsonUtil.parseObj(message) as Map

        def type = result.type
        if (type == null) {
            return
        }

        for (final def entity in innerConsumer.entrySet()) {
            if (entity.key == type) {
                entity.value(result, session)
                return
            }
        }

        log.info("not find mapping to process message, type:　$type")
    }

    @PostConstruct
    void init() {
        // auth start
        innerConsumer.put("auth_required", { message, session ->
            log.info("send token to server...")
            def feedback = [
                    "type"          : "auth"
                    , "access_token": "${haConf.common.token}"
            ]

            session.basicRemote.sendText(JsonUtil.toJson(feedback))
        })

        innerConsumer.put("auth_ok", { result, session ->
            log.info("auth ok...")

            // 订阅状态改变, 先不考虑失败
            def stateChanged = """{"id":${wsId()},"type":"subscribe_events","event_type":"state_changed"}"""
            session.basicRemote.sendText(stateChanged)
        })

        innerConsumer.put("auth_invalid", { result, session ->
            log.info("auth invalid...")
        })
        // auth end

        // request result
        innerConsumer.put("result", { result, session ->
            log.info("command ${result.id} seccuess ${result.seccuess}")
        })

        // state changed
        innerConsumer.put("event", { message, session ->
            def data = message.event?.data as Map
            data == null ?: subscribeProcessService.stateChanged(JsonUtil.conventObj(data, StateChange))
        })

        // trigger
        innerConsumer.put("trigger", { message, session ->
            def variables = message.event?.variables as Map
            if (variables == null) {
                return
            }

        })
    }

    Integer wsId() {
        cacheService.getSeq("ws-id")
    }
}
