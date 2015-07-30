package com.cyngn.vertx.async.promise;

import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * The contract of a discrete action to be executed in a <class>Promise</class>
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 7/30/15
 */
public interface  PromiseAction {
    /**
     * The action to execute.
     *
     * @param context general purpose object for populating with data that can be used by other actions in a promise or
     *                for communicating the result
     * @param onResult the callback that collects the result of any given <class>PromiseAction</class> necessary for
     *                 continuing or completing the chain of actions in a promise.
     */
    void execute(JsonObject context, Consumer<Boolean> onResult) throws IOException;
}

