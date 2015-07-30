package com.cyngn.vertx.async;

/**
 * Represents an action to perform.
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 10/15/14
 */
public interface Action {
    /**
     * Something you want executed
     */
    void callback();
}
