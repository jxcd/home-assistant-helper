package com.me.project.hah.service;

import jakarta.websocket.Session;

/**
 * @author cwj
 * @date 2022/12/5
 */
public interface WsMessageService {

    void onMessage(String message, Session session);
}
