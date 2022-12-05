package com.me.project.hah.service.impl;

import com.me.project.hah.service.CacheService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 内存实现缓存服务, 适用于单体项目
 *
 * @author cwj
 * @date 2022/6/23
 */
@Service
@SuppressWarnings("unchecked")
public class CacheServiceMemoryImpl implements CacheService {

    private final Map<String, Object> keyValue = new ConcurrentHashMap<>();
    private final Map<String, LinkedList<Object>> keyList = new ConcurrentHashMap<>();

    private final Object lockSeq = new Object();

    @Override
    public Integer getSeq(String key) {
        synchronized (lockSeq) {
            final AtomicInteger i = (AtomicInteger) keyValue.computeIfAbsent(key, k -> new AtomicInteger(1));
            return i.getAndIncrement();
        }
    }

    @Override
    public Integer getSeq(String key, int maxValue) {
        return getSeq(key, maxValue, 1);
    }

    @Override
    public Integer getSeq(String key, int maxValue, int minValue) {
        synchronized (lockSeq) {
            final var atomic = (AtomicInteger) keyValue.computeIfAbsent(key, k -> new AtomicInteger(minValue));
            final int i = atomic.getAndIncrement();
            if (i >= maxValue) {
                keyValue.put(key, new AtomicInteger(minValue));
            }
            return i;
        }
    }

    @Override
    public Integer getTempSeq(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getTempSeq(String key, long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cache(String key, Object value) {
        keyValue.put(key, value);
    }

    @Override
    public void cache(String key, Object value, long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) keyValue.get(key);
    }

    @Override
    public void delete(String key) {
        keyValue.remove(key);
    }

    @Override
    public boolean delete(String key, Object value) {
        return keyValue.remove(key, value);
    }

    @Override
    public Long leftPush(String key, Object value) {
        keyList.computeIfAbsent(key, k -> new LinkedList<>()).addFirst(value);
        return 1L;
    }

    @Override
    public Long rightPush(String key, Object value) {
        keyList.computeIfAbsent(key, k -> new LinkedList<>()).addLast(value);
        return 1L;
    }

    @Override
    public <T> T leftPop(String key, Class<T> clazz) {
        return (T) keyList.computeIfAbsent(key, k -> new LinkedList<>()).peek();
    }

    @Override
    public <T> T leftGet(String key, Class<T> clazz) {
        return (T) keyList.computeIfAbsent(key, k -> new LinkedList<>()).getFirst();
    }

    @Override
    public List<Integer> getList(String key, boolean clear) {
        List<Object> list = clear ? keyList.remove(key) : keyList.get(key);
        return (list == null || list.isEmpty())
                ? Collections.emptyList()
                : list.stream().map(String::valueOf).map(Integer::valueOf).collect(Collectors.toList());
    }

    @Override
    public Boolean setNx(String key, Long expireTime) {
        return null;
    }
}
