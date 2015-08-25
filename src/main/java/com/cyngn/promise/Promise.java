package com.cyngn.promise;

import java.util.function.Consumer;

/**
 * Represents a set of one or more asynchronous actions.
 *
 */
public interface Promise<T> {

    String CONTEXT_FAILURE_KEY = "failure";

    /**
     * Executes all actions in parallel
     *
     * @param actions the actions to execute
     * @return the promise representing the actions
     */
    @SuppressWarnings("unchecked")
    Promise<T> all(PromiseAction<T> ... actions);

    /**
     * Executes all actions serially
     *
     * @param actions the actions to execute
     * @return the promise representing the actions
     */
    @SuppressWarnings("unchecked")
    Promise<T> allInOrder(PromiseAction<T> ... actions);

    /**
     * Add an action to execute in the chain.
     *
     * @param action the action to execute
     * @return the promise representing the actions
     */
    Promise<T> then(PromiseAction<T> action);

    /**
     * Add an exception handler to be called in the event something goes wrong.
     *
     * @param onFailure the callback to call on failure
     * @return the promise representing the actions
     */
    Promise<T> except(Consumer<T> onFailure);

    /**
     * The callback to call when all promise actions are done. This will only be called if there are no failures.
     * @param onComplete the callback to hit when the promise is complete
     * @return the promise representing the actions
     */
    Promise<T> done(Consumer<T> onComplete);

    /**
     * A timeout to set on the promise.
     * @param time the delay in milliseconds that the promise needs to complete in
     * @return the promise representing the actions
     */
    Promise<T> timeout(long time);

    /**
     * Has the promise succeeded? Will return false while still executing.
     *
     * @return true if the promise has succeeded false otherwise
     */
    boolean succeeded();

    /**
     * Has the promise completed yet? Either by completing all tasks or failing to.
     *
     * @return true if all actions or done completing or the promise has failed.
     */
    boolean completed();

    /**
     * Called when you are ready to begin resolution of the promise chain.
     *
     * @return the promise you are evaluating
     */
    Promise<T> eval();

    /**
     * If the promise has no actions in it
     *
     * @return true if the promise has no actions, false otherwise
     */
    boolean isEmpty();

}
