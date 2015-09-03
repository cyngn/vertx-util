package com.cyngn.vertx.async;

/**
 * Handles wrapping what occurred as a result of an async operation.
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 9/3/15
 */
public class ResultContext<T> {
    public final boolean succeeded;
    public final Throwable error;
    public final String errorMessage;
    public final T value;

    /**
     * Creates a new result context that represents an operation that completed without error.
     *
     * @param succeeded True if the operation was successful, false otherwise.
     * @param value The resulting value.
     */
    public ResultContext(boolean succeeded, T value) {
        this.succeeded = succeeded;
        this.value = value;
        this.error = null;
        this.errorMessage = null;
    }

    /**
     * Creates a new result context that represents a failed async operation.
     *
     * @param error The {@link Throwable} error that was the result of the operation.
     * @param errorMessage The error message.
     */
    public ResultContext(Throwable error, String errorMessage) {
        this.succeeded = false;
        this.value = null;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a new result context.
     *
     * @param succeeded True if the operation was successful, false otherwise.
     * @param value The resulting value, or null if the operation resulted in an error.
     * @param error The {@link Throwable} error that was the result of the operation.
     * @param errorMessage The error message.
     */
    public ResultContext(boolean succeeded, T value, Throwable error, String errorMessage) {
        this.succeeded = succeeded;
        this.value = value;
        this.error = error;
        this.errorMessage = errorMessage;
    }
}
