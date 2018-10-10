package com.touniba.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * JsonUtils
 */
public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
    }

    /**
     * 对象转json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error("对象转json字符串", e);
        }
        return "";
    }


    /**
     * 字符串转化为对象
     *
     * @param v
     * @param json
     * @return
     */
    public static <T> T toObject(Class<T> v, String json) {
        try {
            return MAPPER.readValue(json.getBytes(), MAPPER.constructType(v));
        } catch (IOException e) {
            LOGGER.error("字符串转化为对象异常", e);
        }
        return null;
    }

    /**
     * 字符串转化为ArrayList对象
     *
     * @param v
     * @param json
     * @return
     */
    public static <T> List<T> toObjectList(Class<T> v, String json) {
        try {
            return MAPPER.readValue(json.getBytes(), MAPPER.getTypeFactory().constructParametricType(ArrayList.class, v));
        } catch (IOException e) {
            LOGGER.error("字符串转化为ArrayList对象异常", e);
        }
        return null;
    }
}