package com.me.project.hah.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    private static final String EMPTY_JSON_OBJECT = "{}";
    public static final ObjectMapper MAPPER;
    private static final JsonSlurper SLURPER = new JsonSlurper();
    private static final JsonGenerator GENERATOR = new JsonGenerator.Options()
            .excludeNulls()
            .build();

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将传入的object转为json
     *
     * @param obj 建议传入JavaBean对象或者LinkedHashMap
     * @return json串
     */
    public static String toJson(Object obj) {
        try {
            return JsonOutput.toJson(obj);
        } catch (Exception e) {
            log.warn("convent to json error, obj: {}, e: {}", obj, e.getMessage());
            return EMPTY_JSON_OBJECT;
        }
    }

    public static String toJsonNoNull(Object obj) {
        try {
            return GENERATOR.toJson(obj);
        } catch (Exception e) {
            log.warn("convent to no null json error, obj: {}, e: {}", obj, e.getMessage());
            return EMPTY_JSON_OBJECT;
        }
    }

    /**
     * 将传入的json转为实体类对象
     *
     * @param json json串
     * @return 返回值对象, 出错时为null
     */
    public static Object parseObj(String json) {
        try {
            return SLURPER.parseText(json);
        } catch (Exception e) {
            log.warn("parse to object error, json: {}, e: {}", json, e.getMessage());
            return null;
        }
    }

    /**
     * 将传入的json转为实体类对象
     *
     * @param json  json串
     * @param clazz 实体类
     * @param <T>   泛型
     * @return 返回值对象, 出错时为null
     */
    public static <T> T parseObj(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.warn("parse to object error, json: {}, class: {}, e: {}", json, clazz, e.getMessage());
            return null;
        }
    }

    /**
     * 将传入的json转为实体类对象
     *
     * @param json  json串
     * @param clazz 实体类
     * @param <T>   泛型
     * @return 返回值对象, 出错时为null
     */
    public static <T> T conventObj(Object json, Class<T> clazz) {
        try {
            return MAPPER.convertValue(json, clazz);
        } catch (IllegalArgumentException e) {
            log.warn("parse to object error, json: {}, class: {}, e: {}", json, clazz, e.getMessage());
            return null;
        }
    }

}
