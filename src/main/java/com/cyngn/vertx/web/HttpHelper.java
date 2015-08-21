package com.cyngn.vertx.web;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.core.MediaType;

/**
 * General Utility functions for dealing with HTTP requests/responses in Vert.x
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 4/3/15
 */
public class HttpHelper {

    /**
     * Send a JSON error response with the specified error and http code.
     *
     * @param error the error that occurred
     * @param response the response being replied to
     * @param code the HTTP code
     */
    public static void processErrorResponse(String error, HttpServerResponse response, int code) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        response.setStatusCode(code);
        Buffer data = Buffer.buffer(new JsonObject().put("error", error).encode());
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length())).write(data).end();
    }

    /**
     * Send a JSON error response with the specified error and http code.
     *
     * @param error the error that occurred in json form
     * @param response the response being replied to
     * @param code the HTTP code
     */
    public static void processErrorResponse(JsonObject error, HttpServerResponse response, int code) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        response.setStatusCode(code);
        Buffer data = Buffer.buffer(error.encode());
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length())).write(data).end();
    }

    /**
     * Send a plain text HTTP 200 response.
     *
     * @param response the response being replied to
     */
    public static void processResponse(HttpServerResponse response) {
        processResponse(response, HttpResponseStatus.OK.code());
    }

    /**
     * Send a plain text HTTP 200 response.
     * @param response the response being replied to
     * @param code the HTTP code
     */
    public static void processResponse(HttpServerResponse response, int code) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        response.setStatusCode(code);
        response.putHeader(HttpHeaders.CONTENT_LENGTH, "0").end();
    }

    /**
     * Send a HTTP 200 response with generic object as JSON.
     *
     * @param value the object to serialize to json
     * @param response the response being replied to
     * @param <T> object type to serialize
     */
    public static <T> void processResponse(T value, HttpServerResponse response) {
        processResponse(value, response, HttpResponseStatus.OK.code());
    }

    /**
     * Send a HTTP 200 response with JSON object.
     *
     * @param obj the json object to send
     * @param response the response being replied to
     */
    public static void processResponse(JsonObject obj, HttpServerResponse response) {
        processResponse(obj, response, HttpResponseStatus.OK.code());
    }

    /**
     * Send a HTTP 200 response with a byte array as an octet stream.
     *
     * @param byteArray the data send
     * @param response the response being replied to
     */
    public static void processResponse(byte[] byteArray, HttpServerResponse response) {
        processResponse(byteArray, response, HttpResponseStatus.OK.code());
    }

    /**
     * Send a HTTP response with JSON object.
     *
     * @param obj the json object to send
     * @param response the response being replied to
     * @param code the HTTP status code to reply with
     */
    public static void processResponse(JsonObject obj, HttpServerResponse response, int code) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        response.setStatusCode(code);
        Buffer data = Buffer.buffer(obj.encode());
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length())).write(data).end();
    }

    /**
     * Send a HTTP response with generic object as JSON.
     *
     * @param value the object to serialize to json
     * @param response the response being replied to
     * @param code the HTTP status code to reply with
     * @param <T> object type to serialize
     */
    public static <T> void processResponse(T value, HttpServerResponse response, int code) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        response.setStatusCode(code);
        Buffer data = Buffer.buffer(JsonUtil.getJsonForObject(value));
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length())).write(data).end();
    }

    /**
     * Send a HTTP response with a byte array as an octet stream.
     *
     * @param byteArray the data send
     * @param response the response being replied to
     * @param code the HTTP status code to reply with
     */
    public static void processResponse(byte[] byteArray, HttpServerResponse response, int code) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
        response.setStatusCode(code);
        Buffer data = Buffer.buffer(byteArray);
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length())).write(data).end();
    }

    /**
     * Attempts to take a json request body string and parse that to the specified class type. If it succeeds it returns
     *  that object. If it fails it responds to the request with a HttpResponseStatus.BAD_REQUEST.
     *
     * @param json the http body to attempt to parse
     * @param clazz the class type to hydrate
     * @param response the associated HTTP response object
     * @param <T> the desired class type
     * @return the object successfully parsed or null in the case where the parsing fails.
     */
    public static <T> T attemptToParse(String json, Class<T> clazz, HttpServerResponse response){
        T result = JsonUtil.parseJsonToObject(json, clazz);
        if (result == null) {
            HttpHelper.processErrorResponse("Failed to parse JSon to create request", response,
                    HttpResponseStatus.BAD_REQUEST.code());
        }

        return result;
    }

    /**
     * Does the response code represent a non-2XX code
     *
     * @param code the code to check
     * @return true if a 2XX code, false otherwise
     */
    public static boolean isHttp2XXResponse(int code) {
        return  code >= 200 && code < 300;
    }
}
