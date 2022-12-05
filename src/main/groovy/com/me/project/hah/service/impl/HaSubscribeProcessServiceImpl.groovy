package com.me.project.hah.service.impl


import com.me.project.hah.dto.ha.StateChange
import com.me.project.hah.service.HaSubscribeProcessService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.util.concurrent.ConcurrentHashMap

@Service
class HaSubscribeProcessServiceImpl implements HaSubscribeProcessService {
    private static final Logger log = LoggerFactory.getLogger(HaSubscribeProcessServiceImpl.class);

    /**
     * 状态改变之后, 根据 entityId 去找处理器集合, 找到之后, 调用处理器集合处理该状态改变
     */
    Map<String, List<Closure>> stateChangeProcessor = new ConcurrentHashMap<>()

    @PostConstruct
    void init() {
        // todo temp, add stateChange sub, 后期改为动态增减
        def sub = { n, o -> log.info("pc_mem_used from ${o.state} -> ${n.state}") }
        stateChangeProcessor.computeIfAbsent("sensor.pc_mem_used", k -> new LinkedList<>()).add(sub)
    }

    @Override
    void stateChanged(StateChange change) {
        def entityId = change?.entityId()
        // 感兴趣的 entityId 才需要被处理
        def processorList = Optional.ofNullable(entityId).map(stateChangeProcessor::get).orElse(Collections.emptyList())
        if (processorList.isEmpty()) {
            return
        }

        def newState = change.newState()
        def oldState = change.oldState()
        log.info("entity ${entityId} from ${oldState.state()} change to ${newState.state()}")
        processorList.each { it(newState, oldState) }
    }
}
