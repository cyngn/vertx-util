package com.cyngn.vertx.client;

import java.util.Map;

/**
 * Abstraction for call request object for {@link ServiceClient}
 *
 * @author asarda@cyngn.com (Ajay Sarda) 11/17/15.
 */
public class ServiceRequest {

    private byte[] payload;
    private long timeout = 0L;
    private Map<String, String> headers;

    public ServiceRequest() {}

    /*
     * Using setters to set the state of object.
     * NOTE:
     * Builder objects are expensive on heap, so please
     * do not add builder object here in future
     */

    /**
     * Gets the payload associated with the request
     *
     * @return - byte array payload
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Sets the payload to be associated with the request
     *
     * @param payload - byte array
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    /**
     * Gets the timeout associated with the request.
     *
     * @return - timeout in milliseconds.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout to be associated with the request.
     *
     * @param timeout - timeout value in milliseconds.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the http headers associated with the request
     *
     * @return - map of string key, string value request headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the http headers to be associated with the request
     *
     * @param headers - map of string key, string value request headers.
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Checks if the {@link ServiceRequest} has timeout value set
     *
     * @return - true if timeout is set, false otherwise.
     */
    public boolean hasTimeout() {
        return timeout > 0L;
    }

    /**
     * Checks if the {@link ServiceRequest} has payload set.
     *
     * @return - true if payload is set, false otherwise.
     */
    public boolean hasPayload() {
        return payload != null;
    }

    /**
     * Checks if the {@link ServiceRequest} has headers set.
     *
     * @return - true if headers are set, false otherwise.
     */
    public boolean hasHeaders() {
        return headers != null && headers.size() > 0;
    }

}
