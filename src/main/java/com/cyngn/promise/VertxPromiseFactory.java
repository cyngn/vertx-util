package com.cyngn.promise;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Handles generating promises that can be executed on the Vert.x eventloop, allows you to not keep track of vert.x
 *  instance.
 *
 */
public class VertxPromiseFactory {

    private final Vertx vertx;

    /**
     * Initialize a promise factory with a reference to your vertx event loop
     * @param vertx the vertx event loop to run your promises on
     */
    public VertxPromiseFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * Create an empty promise.
     *
     * @return a new empty promise
     */
    public Promise<JsonObject> create() {
        return new VertxPromiseImpl(vertx);
    }

    /**
     * Create a promise with a list of actions to be executed serially.
     *
     * @param actions the actions to execute
     * @return the promise representing the actions
     */
    @SuppressWarnings("unchecked")
    public Promise<JsonObject> createSerial(PromiseAction<JsonObject> ... actions) {
        return new VertxPromiseImpl(vertx).allInOrder(actions);
    }

    /**
     * Create a promise with a list of actions to be executed in parallel.
     *
     * @param actions the actions to execute
     * @return the promise representing the actions
     */
    @SuppressWarnings("unchecked")
    public Promise<JsonObject> createParallel(PromiseAction<JsonObject> ... actions) {
        return new VertxPromiseImpl(vertx).all(actions);
    }
}
