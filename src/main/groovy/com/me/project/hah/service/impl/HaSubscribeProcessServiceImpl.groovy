package com.me.project.hah.service.impl

import com.me.project.hah.dto.ha.StateChange
import com.me.project.hah.service.HaSubscribeProcessService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap

@Service
class HaSubscribeProcessServiceImpl implements HaSubscribeProcessService {
    private static final Logger log = LoggerFactory.getLogger(HaSubscribeProcessServiceImpl.class)

    /**
     * 状态改变之后, 根据 entityId 去找处理器集合, 找到之后, 调用处理器集合处理该状态改变
     */
    Map<String, List<Closure>> stateChangeProcessor = new ConcurrentHashMap<>()

    @Override
    void stateChanged(StateChange change) {
        change?.entityId()?.with { stateChangeProcessor.get(it) }?.with {
            def newState = change.newState()
            def oldState = change.oldState()
            log.info("entity ${change.entityId()} from ${oldState.state()} change to ${newState.state()}")
            try {
                it.each { it(newState, oldState) }
            } catch (e) {
                log.error("{} stateChanged error, e: {}", change.entityId(), e.getMessage(), e)
            }
        }
    }

    @Override
    void subscribe(String entityId, Closure listener) {
        stateChangeProcessor.computeIfAbsent(entityId, k -> new LinkedList<>()).add(listener)
    }

    @Override
    void unsubscribe(String entityId, Closure listener) {
        stateChangeProcessor.get(entityId)?.remove(listener)
    }
}
