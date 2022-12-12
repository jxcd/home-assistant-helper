package com.me.project.hah.task;

import com.me.project.hah.dto.ha.listener.StateChangedListener;
import com.me.project.hah.service.HaSubscribeProcessService;
import com.me.project.hah.util.ConfigFileUtil;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 添加对 ha stateChanged 事件的监听者
 *
 * @author cwj
 * @date 2022/12/9
 */
@Component
public class LoadListener {
    private static final Logger log = LoggerFactory.getLogger(LoadListener.class);

    private final Path confFile = Paths.get("config/listener/0.conf");

    @Autowired
    private HaSubscribeProcessService subscribeProcessService;

    private final Map<StateChangedListener, Closure<?>> subscribeCache = new ConcurrentHashMap<>();

    @Scheduled(cron = "30 * * * * ? ")
    public void loadListener() {
        ConfigFileUtil.ifModify(confFile, StateChangedListener::of, list -> {
            var delListener = subscribeCache.keySet().stream().filter(it -> !list.contains(it)).collect(Collectors.toSet());
            delListener.forEach(listener -> {
                var closure = subscribeCache.remove(listener);
                if (closure != null) {
                    subscribeProcessService.unsubscribe(listener.entityId(), closure);
                    log.info("remove stateChanged listener [{}] by listener {}", closure, listener);
                }
            });

            list.forEach(listener -> {
                if (subscribeCache.containsKey(listener)) {
                    return;
                }

                Closure<?> closure;
                try {
                    closure = (Closure<?>) new GroovyShell().evaluate(new File(listener.processFile()));
                } catch (IOException e) {
                    log.error("error eval closure from {}, e: {}", listener.processFile(), e.getMessage());
                    return;
                }
                subscribeProcessService.subscribe(listener.entityId(), closure);
                subscribeCache.put(listener, closure);
                log.info("register stateChanged listener [{}] by listener {}", closure, listener);
            });
        });
    }

}
