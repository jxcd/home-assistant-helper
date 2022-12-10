package com.me.project.hah.connector.ws;

import com.me.project.hah.service.WsMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author cwj
 * @date 2022/8/18
 */
@Component
@ClientEndpoint
public class WsClientEndpoint {
    private static final Logger log = LoggerFactory.getLogger(WsClientEndpoint.class);

    /**
     * 最新的一个session
     */
    private volatile Session session;
    private CountDownLatch countDownLatch = new CountDownLatch(0);

    @Autowired
    private WsMessageService messageService;


    @OnMessage
    public void onMessage(String message, Session session) {
        messageService.onMessage(message, session);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("open session-{}", session.getId());
        this.countDownLatch = new CountDownLatch(1);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("close session-{}, {}", session.getId(), closeReason.getReasonPhrase());
        this.session = null;
        this.countDownLatch.countDown();
    }

    @OnError
    public void onError(Throwable e) {
        log.error("error: {}, {}", session.getId(), e.getMessage(), e);
    }

    public void sendMessage(String message) {
        if (session == null) {
            log.warn("not init, failed send: {}", message);
            return;
        }

        if (!session.isOpen()) {
            log.warn("session not open, failed send: {}", message);
            return;
        }

        try {
            session.getBasicRemote().sendText(message, true);
        } catch (IOException e) {
            log.warn("error to send: {}, e: {}", message, e.getMessage(), e);
        }
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}
