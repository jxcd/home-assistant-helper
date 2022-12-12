package com.me.project.hah.dto.ha.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public record StateChangedListener(
        Integer id,
        String entityId,
        String processFile
) {
    private static final Logger log = LoggerFactory.getLogger(StateChangedListener.class);

    public static StateChangedListener of(String line) {
        String[] sp = line.split(";");
        if (sp.length != 3) {
            log.warn("line error: {}", line);
            return null;
        }
        return new StateChangedListener(Integer.valueOf(sp[0].trim()), sp[1].trim(), sp[2].trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StateChangedListener that = (StateChangedListener) o;
        return Objects.equals(id, that.id) && Objects.equals(entityId, that.entityId) && Objects.equals(processFile, that.processFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityId, processFile);
    }
}
