package com.me.project.hah.dto.ha.listener


import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StateChangedListener {
    private static final Logger log = LoggerFactory.getLogger(StateChangedListener.class)

    Integer id
    String entityId
    String processFile

    static StateChangedListener of(String line) {
        String[] sp = line.split(";")
        if (sp.length != 3) {
            log.warn("line error: {}", line)
            return null
        }

        new StateChangedListener(
                id: Integer.valueOf(sp[0].trim()),
                entityId: sp[1].trim(),
                processFile: sp[2].trim()
        )
    }

}
