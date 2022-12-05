package com.me.project.hah.service


import com.me.project.hah.dto.ha.StateChange

interface HaSubscribeProcessService {

    void stateChanged(StateChange change)

}