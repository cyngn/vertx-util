package com.cyngn.vertx.web;

import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * A collection of helper functions for working with vertx-web.
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 7/15/15
 */
public class RouterTools {

    /**
     * Register a handler on the base router for things that match nothing.
     *
     * @param router the router to add the noMatch routine on
     * @param noMatchHandler what to call if nothing matches
     * @return the added no match Route
     */
    public static Route noMatch(Router router, Handler<RoutingContext> noMatchHandler) {
        return router.route().last(true).handler(noMatchHandler);
    }

    /**
     * Handles adding a number of handlers on the base route, aka match anything route the 'null' route
     *
     * @param router the router to add handlers too
     * @param handlers the handlers to add
     */
    public static void registerRootHandlers(Router router, Handler<RoutingContext> ... handlers) {
        for (Handler<RoutingContext> handler : handlers) {
            router.route().handler(handler);
        }
    }
}
