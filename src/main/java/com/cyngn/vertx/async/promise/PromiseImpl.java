package com.cyngn.vertx.async.promise;

import com.cyngn.vertx.async.Latch;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Implementation of Promise interface.
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 7/30/15
 */
public class PromiseImpl implements Promise {

    private List<PromiseAction> actions;

    private int pos;
    private boolean done;
    private boolean failed;
    private Vertx vertx;
    private Consumer<JsonObject> onFailure;
    private Consumer<JsonObject> onComplete;
    private JsonObject context;
    private Long timerId;
    private AtomicBoolean evaluated;

    // scope to the package
    PromiseImpl(Vertx vertx) {
        this.vertx = vertx;
        pos = 0;
        done = failed = false;
        context = new JsonObject();
        actions = new ArrayList<>();
        evaluated = new AtomicBoolean(false);
    }

    @Override
    public Promise eval(){
        if(actions.size() < 1) {
            throw new IllegalStateException("cannot eval an empty promise");
        }

        if(evaluated.compareAndSet(false, true)) {
            vertx.runOnContext(this::internalEval);
        } else {
            throw new IllegalStateException("You cannot eval a promise chain more than once");
        }
        return this;
    }

    /**
     * Move the promise chain to the next step in the process.
     */
    private void internalEval(Void aVoid) {
        if (!done && pos < actions.size() && !failed) {
            PromiseAction action = actions.get(pos);
            pos++;
            try {
                action.execute(context, (success) -> {
                    if (failed || done) { return; }

                    if (!success) {
                        fail();
                    } else {
                        done = pos == actions.size();
                    }

                    // scheduled the next action
                    if (!done && !failed) {
                        vertx.runOnContext(this::internalEval);
                    }

                    if (done && !failed) {
                        cleanUp();
                        // ultimate success case
                        if(onComplete != null) { onComplete.accept(context); }
                    }
                });
            } catch (Exception ex) {
                context.put(CONTEXT_FAILURE_KEY, ex.toString());
                fail();
            }
        }
    }

    /**
     * End the processing chain due to an error condition
     */
    private void fail() {
        failed = true;
        done = true;
        cleanUp();
        if(onFailure != null) {
            onFailure.accept(context);
        }
    }

    /**
     * Clear local objects no longer needed
     */
    private void cleanUp() {
        cancelTimer();
        actions.clear();
    }

    @Override
    public Promise all(PromiseAction ... theActions) {
        return then((context, onResult) -> {
            // track the results, but execute them all in parallel vs serially
            Latch latch = new Latch(theActions.length, () -> onResult.accept(true));
            for(PromiseAction action : theActions) {
                action.execute(context, (success) -> {
                    if(!success) {
                        onResult.accept(false);
                    } else {
                        latch.complete();
                    }
                });
            }
        });
    }

    @Override
    public Promise allInOrder(PromiseAction... actions) {
        for (PromiseAction action : actions) { then(action); }
        return this;
    }

    @Override
    public Promise then(PromiseAction action) {
        if (done) { throw new IllegalArgumentException("can't add actions to a completed chain"); }

        actions.add(action);
        return this;
    }

    @Override
    public Promise done(Consumer<JsonObject> action) {
        onComplete = action;
        return this;
    }

    @Override
    public Promise timeout(long time) {
        if(done) { throw new IllegalArgumentException("Can't set timer on a completed promise"); }

        if(timerId != null) {
            // if you are able to cancel it schedule another
            if(vertx.cancelTimer(timerId)) {
                timerId = vertx.setTimer(time, theTimerId -> cancel());
            }
        } else {
            timerId = vertx.setTimer(time, theTimerId -> cancel());
        }

        return this;
    }

    /**
     * Get rid of a timer that has not been fired yet.
     */
    private void cancelTimer(){
        if(timerId != null) {
            System.out.println("cancelling timer");
            vertx.cancelTimer(timerId);
        }
    }

    /**
     * Function called when a timer is expired but the chain is not yet complete.
     */
    private void cancel() {
        timerId = null;
        if(!done) {
            context.put(CONTEXT_FAILURE_KEY, "promise timed out");
            fail();
        }
    }

    @Override
    public boolean succeeded() {
        return !failed;
    }

    @Override
    public boolean completed() {
        return done;
    }

    @Override
    public Promise except(Consumer<JsonObject> onFailure) {
        this.onFailure = onFailure;
        return this;
    }
}