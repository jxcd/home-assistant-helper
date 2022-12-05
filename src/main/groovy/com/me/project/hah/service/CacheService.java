package com.me.project.hah.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface CacheService {

    /**
     * 获取唯一序列
     *
     * @param key key
     * @return 序列
     */
    Integer getSeq(String key);

    /**
     * 获取序列
     * 到达最大值后, 回归1
     *
     * @param key      key
     * @param maxValue 序列最大值
     * @return 序列
     */
    Integer getSeq(String key, int maxValue);

    /**
     * 获取序列
     * 到达最大值后, 回归最小值
     *
     * @param key      key
     * @param maxValue 序列最大值
     * @param minValue 序列最小值
     * @return 序列
     */
    Integer getSeq(String key, int maxValue, int minValue);

    /**
     * 获取临时的序列, 适合一段时间内的计数
     * 超时之后该key会自动删除
     *
     * @param key key, 建议使用 z_temp_ 前缀
     * @return 序列
     */
    Integer getTempSeq(String key);

    /**
     * 获取临时的序列, 适合一段时间内的计数
     * 超时之后该key会自动删除
     *
     * @param key      key, 建议使用 z_temp_ 前缀
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @return 序列
     */
    Integer getTempSeq(String key, long timeout, TimeUnit timeUnit);

    /**
     * 缓存键值
     *
     * @param key   键
     * @param value 值
     */
    void cache(String key, Object value);

    /**
     * 缓存键值
     *
     * @param key      键
     * @param value    值
     * @param timeout  缓存时间
     * @param timeUnit 缓存时间单位
     */
    void cache(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * 从缓存中查找值
     *
     * @param key   键
     * @param clazz 值的类型
     * @param <T>   值的类型
     * @return 值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 从缓存中删除
     *
     * @param key 键
     */
    void delete(String key);

    /**
     * 从缓存中删除
     *
     * @param key 键
     * @return
     */
    boolean delete(String key, Object value);

    /**
     * 向队列头部设值
     *
     * @param key   键
     * @param value 值
     * @return 序号
     */
    Long leftPush(String key, Object value);

    /**
     * 向队列尾部设值
     *
     * @param key   键
     * @param value 值
     * @return 序号
     */
    Long rightPush(String key, Object value);

    /**
     * 从队列头部取值并删除该值
     *
     * @param key   键
     * @param clazz 值的类型
     * @param <T>   值的类型
     * @return 值
     */
    <T> T leftPop(String key, Class<T> clazz);

    /**
     * 从队列头部取值但不删除该值
     *
     * @param key   键
     * @param clazz 值的类型
     * @param <T>   值的类型
     * @return 值
     */
    <T> T leftGet(String key, Class<T> clazz);

    /**
     * 获取list
     *
     * @param key   键
     * @param clear 获取后是否清空?
     * @return 返回获取的list
     */
    List<Integer> getList(String key, boolean clear);

    /**
     * 原子设置唯一标识符
     * @param key 标识符
     * @param expireTime 过期时间
     * @return
     */
    Boolean setNx(String key,Long expireTime);
}
