package com.cyngn.vertx.web;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;

/**
 * Basic Rest interface to use in Vert.x
 *
 * @author truelove@cyngn.com (Jeremy Truelove) 10/15/14
 */
public interface RestApi {
    /**
     * Handle adding your APIs to the server's router
     *
     * @param router the object that does routing of requests to endpoint handlers
     * @return the initialized rest api
     */
    default RestApi init(Router router) {
        if (supportedApi() != null) {
            for (RestApiDescriptor api : supportedApi()) {
                router.route(api.method, api.uri).handler(api.handler);
            }
        }

        return this;
    }

    /**
     * What APIs are currently being exposed by the implementation
     *
     * @return the API list
     */
    RestApiDescriptor [] supportedApi();

    /**
     * Dump the supported API
     *
     * @param logger the logger to use for output
     */
    default void outputApi(Logger logger) {
        for (RestApiDescriptor anApi : supportedApi() ) {
            logger.info("{} - {}", anApi.method, anApi.uri);
        }
    }

    /**
     * A way to describe an API
     */
    class RestApiDescriptor {
        public final HttpMethod method;
        public final String uri;
        public final Handler<RoutingContext> handler;

        public RestApiDescriptor(HttpMethod method, String uri, Handler<RoutingContext> handler) {
            this.method = method;
            this.uri = uri;
            this.handler = handler;


        }
    }
}
