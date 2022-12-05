package com.me.project.hah.config;

import com.me.project.hah.connector.ws.WsClientEndpoint;
import com.me.project.hah.dto.conf.HaConf;
import com.me.project.hah.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

/**
 * @author cwj
 * @date 2022/8/18
 */
@Configuration
public class WebSocketConfig {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Bean
    public WsClientEndpoint haWsEndpoint(HaConf haConf) {
        WsClientEndpoint endpoint = new WsClientEndpoint();
        initHaWsConnect(haConf.getCommon().getUrlWs(), endpoint);
        return endpoint;
    }

    /**
     * 初始化 url和endpoint
     *
     * @param url      ws连接地址
     * @param endpoint 端点
     */
    private void initHaWsConnect(String url, WsClientEndpoint endpoint) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Runnable wsConnTask = () -> {
            while (true) {
                try (Session session = container.connectToServer(endpoint, new URI(url))) {
                    log.info("connect success: {}", session.getId());
                    // 等待直到断开, 断开之后, 等段一段时间, 重连
                    endpoint.getCountDownLatch().await();
                } catch (InterruptedException e) {
                    log.info("interrupted on ws session, e: {}", e.getMessage());
                    break;
                } catch (Exception e) {
                    log.warn("find exception on ws session: {}", e.getMessage());
                }
                ThreadUtil.sleep(2000L);
                log.info("try re-connect ws...");
            }

            log.info("ws over...");
        };
        ThreadUtil.createThread("ws-conn", wsConnTask).start();
    }

}
