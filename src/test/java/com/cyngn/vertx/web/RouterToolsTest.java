package com.cyngn.vertx.web;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author truelove@cyngn.com (Jeremy Truelove) 7/15/15
 */
@RunWith(VertxUnitRunner.class)
public class RouterToolsTest {

    @Test
    public void testNoMatch(TestContext testContext) {

        Vertx vertx = Vertx.vertx();

        Router r = Router.router(vertx);

        r.route().handler(LoggerHandler.create());
        r.route().handler(BodyHandler.create());

        r.get("/someRandomPath").handler(context -> {});

        Handler<RoutingContext> foo = context -> {};
        Route noMatch = RouterTools.noMatch(r, foo);

        testContext.assertEquals(4, r.getRoutes().size());

        // no match should be last since we set it to the last handler on the router
        testContext.assertEquals(noMatch, r.getRoutes().get(3));
    }

    @Test
    public void testRootHandlers(TestContext testContext) {

        Vertx vertx = Vertx.vertx();

        Router r = Router.router(vertx);

        RouterTools.registerRootHandlers(r, LoggerHandler.create(), BodyHandler.create());
        r.post("/fooBar").handler(context -> {});

        testContext.assertEquals(3, r.getRoutes().size());

        // no match should be last since we set it to the last handler on the router
        testContext.assertEquals(null, r.getRoutes().get(0).getPath());
        testContext.assertEquals(null, r.getRoutes().get(1).getPath());
        testContext.assertEquals("/fooBar", r.getRoutes().get(2).getPath());
    }
}
