package com.cyngn.vertx.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Collection of utils for parsing Json and interacting with it.
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 10/15/14
 */
public class JsonUtil {
    private final static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public final static ObjectMapper mapper = new ObjectMapper();

    /**
     * Parses raw json into a concrete impl of your choosing
     *
     * @param data  the json raw data
     * @param clazz the class to parse the json into
     * @param <T>   the type of class parameterizing this method
     * @return the new instance object generated from json or null on failure
     */
    public static <T> T parseJsonToObject(String data, Class<T> clazz) {
        if (data == null || "".equals(data)) { return null; }

        T obj = null;
        try { obj = mapper.readValue(data, clazz); }
        catch (IOException e) {
            logger.error("Error parsing class: {} error: ", clazz, e);
        }
        return obj;
    }

    /**
     * Parses raw json bytes into a concrete impl of your choosing. If there is an error parsing then the
     * exception is caught and the result will be null.
     *
     * @param data  the json raw data
     * @param clazz the class to parse the json into
     * @param <T>   the type of class parameterizing this method
     * @return the new instance object generated from json or null on failure
     */
    public static <T> T parseJsonToObject(byte[] data, Class<T> clazz) {
        if (data == null) {
            return null;
        }

        T obj = null;
        try {
            obj = mapper.readValue(data, clazz);
        } catch (IOException e) {
            logger.error("Error parsing class: {} error: ", clazz, e);
        }
        return obj;
    }

    /**
     * Serializes object to raw json
     *
     * @param object the object to create Json from
     * @return the Json representing the object passed in or null if we fail to be able to generate it
     */
    public static String getJsonForObject(Object object) {
        if (object == null) { throw new IllegalArgumentException("Can't serialize a null object to Json."); }

        String jsonString = null;
        try { jsonString = mapper.writeValueAsString(object); }
        catch (JsonProcessingException e) {
            logger.error("Error generating JSON class: {} error: ", object.getClass().getName(), e);
        }
        return jsonString;
    }
}
