package com.cyngn.vertx.async.promise;

import io.vertx.core.Vertx;

/**
 * Handles generating promises that can be executed on the Vert.x eventloop
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 7/30/15
 */
public class PromiseFactory {

    private final Vertx vertx;

    /**
     * Initialize a promise factory with a reference to your vertx event loop
     * @param vertx the vertx event loop to run your promises on
     */
    public PromiseFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * Create a standard promise with a list of actions to be executed serially.
     *
     * @param actions the actions to execute
     * @return the promise representing the actions
     */
    public Promise create(PromiseAction ... actions) {
        return new PromiseImpl(vertx, actions);
    }

    /**
     * Create a standard promise with a list of actions to be executed in parallel.
     *
     * @param actions the actions to execute
     * @return the promise representing the actions
     */
    public Promise createParallel(PromiseAction ... actions) {
        return new PromiseImpl(vertx, true, actions);
    }
}
