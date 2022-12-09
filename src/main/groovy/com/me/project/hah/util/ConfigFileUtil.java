package com.me.project.hah.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 动态配置文件
 *
 * @author cwj
 * @date 2022/12/9
 */
public class ConfigFileUtil {
    private static final Logger log = LoggerFactory.getLogger(ConfigFileUtil.class);

    private static final Map<Path, FileTime> CACHE_LAST_MODIFIED_TIME_MAP = new ConcurrentHashMap<>();

    /**
     * 将配置文件的每一行转为配置对象
     *
     * @param confFile   配置文件
     * @param lineToBean 行转对象
     * @param <T>        对象
     * @return 对象集合
     */
    public static <T> List<T> loadConfigFile(Path confFile, Function<String, T> lineToBean) {
        if (!Files.exists(confFile)) {
            return Collections.emptyList();
        }

        List<T> list;
        try (Stream<String> lines = Files.lines(confFile)) {
            Predicate<String> note = s -> s.startsWith("#");
            list = lines
                    .filter(StringUtils::hasText)
                    .filter(note.negate())
                    .map(lineToBean)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("failed read lines, e: {}", e.getMessage());
            list = Collections.emptyList();
        }
        return list;
    }

    /**
     * 如果 confFile 发生变化, 就先用 lineToBean 解析后, 调用 process 处理
     *
     * @param confFile   配置文件
     * @param lineToBean 行转对象
     * @param process    处理
     * @return 是否 process 并完成
     */
    public static <T> boolean ifModify(Path confFile, Function<String, T> lineToBean, Consumer<List<T>> process) {
        boolean exists = Files.exists(confFile);
        if (!exists) {
            log.info("not find {}", confFile.toAbsolutePath());
            return false;
        }

        FileTime lastModifiedTime;
        try {
            lastModifiedTime = Files.getLastModifiedTime(confFile);
            if (Objects.equals(CACHE_LAST_MODIFIED_TIME_MAP.get(confFile), lastModifiedTime)) {
                log.debug("{} not change, skip", confFile);
                return false;
            }
        } catch (IOException e) {
            log.warn("load lastModifiedTime error, {}, e: {}", confFile.toAbsolutePath(), e.getMessage());
            return false;
        }

        log.info("{} is changed, parse", confFile);
        final List<T> list = loadConfigFile(confFile, lineToBean);
        log.info("parse success");
        try {
            process.accept(list);
            log.info("process success");
        } catch (Exception e) {
            log.warn("process error, e: {}", e.getMessage());
            return false;
        }

        CACHE_LAST_MODIFIED_TIME_MAP.put(confFile, lastModifiedTime);
        return true;
    }

}
