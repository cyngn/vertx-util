package com.cyngn.vertx.async;

/**
 * A helper class for coordinating events asynchronously.
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 10/15/14
 */
public class Latch {
    private Action onComplete;
    private int count;
    private int currentCount;

    /**
     * @param count the number of events to complete before callback is called
     * @param onComplete the action to take when the latch has been completed
     */
    public Latch(int count, Action onComplete) {
        validateParams(count, onComplete);

        this.count = count;
        currentCount = 0;
        this.onComplete = onComplete;
    }

    /**
     * Called to signal to the latch an event has completed.
     */
    public void complete() {
        if (currentCount == count) {
            throw new IllegalStateException("Latch has already been completed.");
        }

        currentCount++;

        if (currentCount == count) {
            onComplete.callback();
        }
    }

    /**
     * Resets the latch using the current settings.
     */
    public void reset() {
        reset(count, onComplete);
    }

    /**
     * Resets the latch using a new count.
     */
    public void reset(int count) {
        reset(count, onComplete);
    }

    /**
     * Resets the latch to a new count and new action
     */
    public void reset(int count, Action onComplete) {
        validateParams(count, onComplete);

        currentCount = 0;
        this.count = count;
        this.onComplete = onComplete;
    }

    private void validateParams(int count, Action onComplete) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }

        if (onComplete == null) {
            throw new IllegalArgumentException("Cannot set a null callback for complete");
        }
    }
}
