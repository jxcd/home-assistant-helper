package com.me.project.hah.task;

import com.me.project.hah.dto.ScheduleTaskConfig;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 定时检索配置文件, 存在则加载其中的内容
 * 1. id;enable(0/1);cron;TaskFile
 * 读取定时任务后
 * 1. enable, 缓存中有则不管, 缓存中无则生成task, 维护 TaskLine->task 的缓存
 * 2. disable, 缓存中有则取消, 缓存中无则不管
 * 3. 如果有不存在的TaskLine对应缓存, 则取消该任务*
 *
 * @author cwj
 * @date 2022/12/3
 */
@Component
public class LoadTask {
    private static final Logger log = LoggerFactory.getLogger(LoadTask.class);

    @Autowired
    TaskScheduler taskScheduler;

    Path confFile = Paths.get("task/0.conf");
    private FileTime last = null;
    private final Map<ScheduleTaskConfig, ScheduledFuture<?>> taskCache = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 * * * * ? ")
    public void loadTask() {
        boolean exists = Files.exists(confFile);
        if (!exists) {
            log.info("not find 0.conf, {}", confFile.toAbsolutePath());
            return;
        }

        FileTime lastModifiedTime = null;
        try {
            lastModifiedTime = Files.getLastModifiedTime(confFile);
            if (Objects.equals(this.last, lastModifiedTime)) {
                log.debug("0.conf not change, skip");
                return;
            }
            log.info("0.conf is changed, parse");
        } catch (IOException e) {
            log.warn("load lastModifiedTime error, {}, e: {}", confFile.toAbsolutePath(), e.getMessage());
        }

        try (Stream<String> lines = Files.lines(confFile)) {
            Set<ScheduleTaskConfig> lineConfigSet = lines
                    .filter(StringUtils::hasText)
                    .map(this::parseLine).filter(Objects::nonNull)
                    .peek(this::schedule).collect(Collectors.toSet());

            taskCache.forEach((config, future) -> {
                if (!lineConfigSet.contains(config) && future != null) {
                    future.cancel(false);
                    taskCache.remove(config, future);
                    log.info("remove schedule task [{}] by config {}", future, config);
                }
            });

        } catch (IOException e) {
            log.warn("failed read lines, e: {}", e.getMessage());
        }

        this.last = lastModifiedTime;
    }

    private ScheduleTaskConfig parseLine(String line) {
        try {
            return ScheduleTaskConfig.of(line);
        } catch (Exception e) {
            log.warn("failed parse line: {}, e: {}", line, e.getMessage());
            return null;
        }
    }

    private void schedule(ScheduleTaskConfig config) {
        boolean enable = config.enable();
        ScheduledFuture<?> future = taskCache.get(config);

        if (enable) {
            if (future == null) {
                ScheduledFuture<?> schedule = taskScheduler.schedule(() -> this.execute(config.taskFile()), new CronTrigger(config.pattern()));
                log.info("create schedule task [{}] by config {}", schedule, config);
                taskCache.put(config, schedule);
            }

        } else {
            if (future != null) {
                future.cancel(false);
                taskCache.remove(config, future);
                log.info("disable schedule task [{}] by config {}", future, config);
            }
        }
    }

    private void execute(String file) {
        GroovyShell shell = new GroovyShell();
        try {
            shell.evaluate(new File(file));
        } catch (IOException e) {
            log.warn("failed execute {}, e: {}", file, e.getMessage(), e);
        }
    }

}
