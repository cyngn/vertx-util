package com.cyngn.vertx.async.promise;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for PromisesImpl
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 7/30/15
 */
@RunWith(VertxUnitRunner.class)
public class PromiseImplTests {

    private Vertx vertx;

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
    }

    @After
    public void after(TestContext context) {
        vertx.close();
    }

    @Test
    public void testBasic(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        List<Integer> foo = new ArrayList<>();

        factory.create((taskContext, onComplete) -> {
            foo.add(1);
            onComplete.accept(true);
        }).then((taskContext, onComplete) -> {
            foo.add(2);
            onComplete.accept(true);
        }).then((taskContext, onComplete) -> {
            foo.add(5);
            taskContext.put("data", foo);
            onComplete.accept(true);
        }).done((taskContext) -> {
            context.assertTrue(taskContext != null);
            context.assertTrue(taskContext.containsKey("data"));
            context.assertEquals(3, taskContext.getJsonArray("data").size());
            context.assertEquals(1, taskContext.getJsonArray("data").getInteger(0));
            context.assertEquals(5, taskContext.getJsonArray("data").getInteger(2));
            async.complete();
        }).eval();
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleEval(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);
        factory.create((taskContext, onComplete) -> {
            onComplete.accept(true);
        }).eval().eval();
    }

    @Test(expected = IllegalStateException.class)
    public void testEvalOnEmptyPromise(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Promise.newInstance(vertx).eval();
    }

    @Test
    public void testParallel(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        List<Integer> foo = new ArrayList<>();

        factory.createParallel((taskContext, onComplete) -> {

            vertx.executeBlocking((future) -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
                future.complete();
            }, asyncResult -> {
                foo.add(1);
                onComplete.accept(true);
            });
        }, (taskContext, onComplete) -> {
            foo.add(2);
            taskContext.put("data", foo);
            onComplete.accept(true);
        }).done((taskContext) -> {
            context.assertTrue(taskContext != null);
            context.assertTrue(taskContext.containsKey("data"));
            context.assertEquals(2, taskContext.getJsonArray("data").size());
            context.assertEquals(2, taskContext.getJsonArray("data").getInteger(0));
            context.assertEquals(1, taskContext.getJsonArray("data").getInteger(1));
            async.complete();
        }).eval();
    }

    @Test
    public void testAllInOrder(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        List<Integer> foo = new ArrayList<>();

        factory.createParallel((taskContext, onComplete) -> {

            vertx.executeBlocking((future) -> {
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {}
                future.complete();
            }, asyncResult -> {
                foo.add(1);
                onComplete.accept(true);
            });
        }).allInOrder((taskContext, onComplete) -> {
            vertx.executeBlocking((future) -> {
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
                future.complete();
            }, asyncResult -> {
                foo.add(3);
                onComplete.accept(true);
            });
        }, (taskContext, onComplete) -> {
            foo.add(2);
            taskContext.put("data", foo);
            onComplete.accept(true);
        }).done((taskContext) -> {
            context.assertTrue(taskContext != null);
            context.assertTrue(taskContext.containsKey("data"));
            context.assertEquals(3, taskContext.getJsonArray("data").size());
            context.assertEquals(1, taskContext.getJsonArray("data").getInteger(0));
            context.assertEquals(3, taskContext.getJsonArray("data").getInteger(1));
            context.assertEquals(2, taskContext.getJsonArray("data").getInteger(2));
            async.complete();
        }).eval();
    }

    @Test
    public void testExcept(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        factory.create((taskContext, onComplete) -> {
            taskContext.put("reason", "something bad");
            onComplete.accept(false);
        }, (taskContext, onComplete) -> {
            context.fail("This should never be reached");
        }).done((taskContext) -> {
            context.fail("shouldn't call done on failure");
        }).except(taskContext -> {
            context.assertTrue(taskContext != null);
            context.assertTrue(taskContext.containsKey("reason"));
            async.complete();
        }).eval();
    }

    @Test
    public void testTimeout(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        factory.create((taskContext, onComplete) -> {
            // do nothing, aka don't hit the callback
        }, (taskContext, onComplete) -> {
            context.fail("This should never be reached");
        }).done((taskContext) -> {
            context.fail("shouldn't call done on failure");
        }).timeout(1000)
        .except(taskContext -> {
            context.assertTrue(taskContext != null);
            context.assertTrue(taskContext.containsKey(Promise.CONTEXT_FAILURE_KEY));
            context.assertTrue(taskContext.getString(Promise.CONTEXT_FAILURE_KEY).indexOf("timed out") != -1);
            async.complete();
        }).eval();
    }

    @Test
    public void testExceptionOnCallback(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        factory.create((taskContext, onComplete) -> {
            throw new IOException();
        }, (taskContext, onComplete) -> {
            context.fail("This should never be reached");
        }).done((taskContext) -> {
            context.fail("shouldn't call done on failure");
        }).except((taskContext) -> {
            context.assertTrue(taskContext != null);
            context.assertTrue(taskContext.containsKey(Promise.CONTEXT_FAILURE_KEY));
            context.assertTrue(taskContext.getString(Promise.CONTEXT_FAILURE_KEY).indexOf("IOException") != -1);
            async.complete();
        }).eval();
    }
}