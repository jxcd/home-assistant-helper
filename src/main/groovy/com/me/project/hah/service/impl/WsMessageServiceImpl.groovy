package com.me.project.hah.service.impl

import com.me.project.hah.dto.conf.HaConf
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

    /**
     * 内部处理器在这里添加
     */
    Map<String, Closure> innerConsumer = new ConcurrentHashMap<>();

    @Override
    void onMessage(String message, Session session) {
        log.info("from session-{} rec: {}", session.getId(), message)

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
        innerConsumer.put("auth_required", { message, session ->
            log.info("send token to server...")
            def feedback = [
                    "type"          : "auth"
                    , "access_token": "${haConf.common.token}"
            ]

            session.basicRemote.sendText(JsonUtil.toJson(feedback))
        })

        innerConsumer.put("auth_ok", { message, session ->
            log.info("auth ok...")
        })

        innerConsumer.put("auth_invalid", { message, session ->
            log.info("auth invalid...")
        })
    }
}
