package com.me.project.hah.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author cwj
 * @date 2022/12/3
 */
public record ScheduleTaskConfig(Integer id, boolean enable, String pattern, String taskFile) {
    private static final Logger log = LoggerFactory.getLogger(ScheduleTaskConfig.class);

    /**
     * 解析配置行为调度对象
     * id;enable(0/1);cron;TaskFile
     *
     * @param line 配置行
     * @return 对象
     */
    public static ScheduleTaskConfig of(String line) {
        String[] sp = line.split(";");
        if (sp.length != 4) {
            log.warn("line error: {}", line);
            return null;
        }

        return new ScheduleTaskConfig(
                Integer.valueOf(sp[0].trim()),
                Integer.parseInt(sp[1].trim()) != 0,
                sp[2].trim(),
                sp[3].trim()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleTaskConfig that = (ScheduleTaskConfig) o;
        return enable == that.enable && Objects.equals(id, that.id) && Objects.equals(pattern, that.pattern) && Objects.equals(taskFile, that.taskFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enable, pattern, taskFile);
    }
}
