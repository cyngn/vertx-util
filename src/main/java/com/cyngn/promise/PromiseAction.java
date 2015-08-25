package com.cyngn.promise;

import java.util.function.Consumer;

/**
 * The contract of a discrete action to be executed in a Promise
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 7/30/15
 */
public interface  PromiseAction<T> {
    /**
     * The action to execute.
     *
     * @param context general purpose object for populating with data that can be used by other actions in a promise or
     *                for communicating the result
     * @param onResult the callback that collects the result of any given PromiseAction necessary for
     *                 continuing or completing the chain of actions in a promise.
     */
    void execute(T context, Consumer<Boolean> onResult);
}

