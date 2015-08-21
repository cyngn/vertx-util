package com.cyngn.vertx.async.promise;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        factory.createSerial((taskContext, onComplete) -> {
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
        factory.createSerial((taskContext, onComplete) -> onComplete.accept(true)).eval().eval();
    }

    @Test(expected = IllegalStateException.class)
    public void testEvalOnEmptyPromise(TestContext context) {
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
                } catch (Exception ex) {
                }
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

        factory.createSerial((taskContext, onComplete) -> {
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

        factory.createSerial((taskContext, onComplete) -> {
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
    public void testTimeoutCancelled(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        AtomicInteger count = new AtomicInteger(0);

        Promise promise = factory.createSerial((taskContext, onComplete) -> {
            count.incrementAndGet();
            onComplete.accept(true);
        }, (taskContext, onComplete) -> {
            count.incrementAndGet();
            onComplete.accept(true);
        }).done((taskContext) -> count.incrementAndGet())
        .timeout(500)
        .except(taskContext -> {
            context.fail("We should not get here due to timeout");
        }).eval();

        vertx.setTimer(2000, (timer) -> {
            context.assertEquals(3, count.get());
            context.assertTrue(promise.succeeded());
            async.complete();
        });
    }

    @Test
    public void testExceptionOnCallback(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);

        Async async = context.async();

        factory.createSerial((taskContext, onComplete) -> {
            throw new RuntimeException();
        }, (taskContext, onComplete) -> {
            context.fail("This should never be reached");
        }).done((taskContext) -> {
            context.fail("shouldn't call done on failure");
        }).except((taskContext) -> {
            context.assertTrue(taskContext != null);
            context.assertTrue(taskContext.containsKey(Promise.CONTEXT_FAILURE_KEY));
            context.assertTrue(taskContext.getString(Promise.CONTEXT_FAILURE_KEY).indexOf("RuntimeException") != -1);
            async.complete();
        }).eval();
    }

    @Test
    public void testIsEmpty(TestContext context) {
        PromiseFactory factory = new PromiseFactory(vertx);
        Promise p = factory.create().then((pContext, onResult) -> {});

        context.assertFalse(p.isEmpty());

        Promise p2 = factory.create();

        context.assertTrue(p2.isEmpty());
    }

}