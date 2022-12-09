package com.me.project.hah.service


import com.me.project.hah.dto.ha.StateChange

interface HaSubscribeProcessService {

    void stateChanged(StateChange change)

    void subscribe(String entityId, Closure listener)

    void unsubscribe(String entityId, Closure listener)
}