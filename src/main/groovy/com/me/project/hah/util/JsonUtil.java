package com.me.project.hah.util;

import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    private static final String EMPTY_JSON_OBJECT = "{}";
    private static final JsonSlurper slurper = new JsonSlurper();
    private static final JsonGenerator generator = new JsonGenerator.Options()
            .excludeNulls()
            .build();

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

    /**
     * 将传入的json转为实体类对象
     *
     * @param json json串
     * @return 返回值对象, 出错时为null
     */
    public static Object parseObj(String json) {
        try {
            return slurper.parseText(json);
        } catch (Exception e) {
            log.warn("parse to object error, json: {}, e: {}", json, e.getMessage());
            return null;
        }
    }

    public static String toJsonNoNull(Object obj) {
        try {
            return generator.toJson(obj);
        } catch (Exception e) {
            log.warn("convent to no null json error, obj: {}, e: {}", obj, e.getMessage());
            return EMPTY_JSON_OBJECT;
        }
    }
}
