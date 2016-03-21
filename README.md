[![Build Status](https://travis-ci.org/cyngn/vertx-util.svg?branch=master)](https://travis-ci.org/jtruelove/vertx-util)

# vertx-util

General purpose utils & apis for interacting with vert.x

## Getting Started

Add a dependency to vertx-util:

```xml
<dependency>
    <groupId>com.cyngn.vertx</groupId>
    <artifactId>vertx-util</artifactId>
    <version>0.7.0</version>
</dependency>
```

| vertx-util | vert.x Version |
| ------- | --------------:|
| 3.3.0-SNAPSHOT   | 3.3.0-SNAPSHOT |
| 0.6.0   | 3.2.0          |

## Promises

Light weight promises that run on the vertx event loop. This allows developers to easily coordinate running a number of callbacks in parallel or serially while getting  notifications of results or exceptions. Also there is a JsonObject that is supplied to enable passing of information between callbacks as well as to the exception or done handlers.

### Basic Example

```java
// pass it the ref to your current vertx event loop.
Promise.newInstance(vertx)
.then((context, onResult) -> {
    // do some stuff
    context.put("result", "some text to share");
    onResult.accept(true);
})
.then((context, onResult) -> onResult.accept(context.containsKey("result")))
// optional exception handler, when a promise calls onResult.accept(false) or a callback throws an exception
.except((context) -> System.out.println("Failure: " + context.encode()))
// optional completion handler called when all callbacks have run and succeeded
.done((context) -> System.out.println("Success: " + context.encode()))
// optionally set a timeout in ms for the callback chain to complete in
.timeout(3000)
// you are required to call this once and only once to make the promise chain begin to evaluate
.eval();
```

### Callbacks in Parallel

```java
// pass it the ref to your current vertx event loop,
Promise.newInstance(vertx)
// these can complete in a different order than they are added
.all((context, onResult) -> {
    System.out.println("Also 'all' call 1");
    onResult.accept(true);
},
(context, onResult) -> {
    System.out.println("Also 'all' call 2");
    onResult.accept(true);
})
.done((context) -> System.out.println("Success"))
// you are required to call this once and only once to make the promise chain begin to evaluate
.eval();
```

### Callbacks Serially then in Parallel

```java
// pass it the ref to your current vertx event loop,
Promise.newInstance(vertx)
.then((context, onResult) -> {
    System.out.println("Start here");
    onResult.accept(true);
})
.then((context, onResult) -> {
    System.out.println("Continue here");
    onResult.accept(true);
})
.all((context, onResult) -> {
    System.out.println("Starting something else");
    vertx.executeBlocking(future -> {
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {}
            future.complete();
        }, asyncResult -> {
            System.out.println("'all' call 1");
            onResult.accept(true);
    });
    }, (context, onResult) -> {
        System.out.println("'all' call 2");
        onResult.accept(true);
    })
.done((context) -> System.out.println("Success"))
// you are required to call this once and only once to make the promise chain begin to evaluate
.eval();
```

### Promise Factory

There's a promise factory supplied that allows you to set the vertx instance once and generate Promises on demand without having to keep your vertx reference around.

```java
PromiseFactory factory = new PromiseFactory(vertx);
// Promise 1
factory.create().then((context, onResult) -> {
   System.out.println("a new promise");
    onResult.accept(true);
}).eval();

// Promise 2
factory.createParallel((context, onResult) -> {
    System.out.println("a test");
    onResult.accept(true);
},(context, onResult) -> {
    System.out.println("a test 2");
    onResult.accept(true);
}).eval();
```

### Things to Remember

* you must call `eval()` after creating your chain
* you must call the `onResult` callback with true or false to continue the chain processing
* in the case of a timeout the exception handler is called

## Latches
These offer a way to coordinate an action after `N` events have completed just using the vert.x event loop and no additional threads.

```java
// this callback will fire after complete has been called on the latch twice
Latch latch = new Latch(2, () -> System.out.println("I'm all done now"));

// call #1
vertx.executeBlocking(future -> {
    // something expensive like a DB call
    future.complete();
}, result -> latch.complete());

vertx.setTimer(2000, (aTimerId) -> latch.complete());
```

## Event Bus Tools
There are a number of event bus functions including to assist in consuming messages one or `N` times.

```java
// ie one shot consumers of events
EventBusTools.oneShotConsumer(bus, "SOME_ADDRESS", event -> {
    System.out.println("Got an event: " + event);
});
```

## Service Client
Service Client is wrapper over vertx http client. It supports

* Creating http client from Json Configuration and builder.
* Specification of timeout for apis.
* In-built retry handler ( coming later)

```json
{
  "host" : "localhost",
  "port" : 8080,
  "num_connections" : 10,
  "apis" :[
    {
      "name" : "put",
      "timeout" : 1000
    },
    {
      "name" : "remove",
      "timeout" : 1000
    }
  ]
}
```

Field breakdown:
* `host` server host or endpoint to connect to
* `port` server port to connect to
* `num_connections` number of connections in connection pool for Vertx http client
* `apis` timeouts for apis (extensible to other attributes in future)

Configuration Example:
```java
    JsonObject config = new JsonObject();
    config.put(ServiceClient.HOST, "localhost");
    config.put(ServiceClient.PORT, 8080);
    ServiceClient.create(vertx, config);
```

Builder Example:

```java
    ServiceClient.Builder builder = new ServiceClient.Builder(vertx);
        builder.withHost("localhost").withPort(8080);
        builder.addApiTimeout("put", 1000L);
        builder.addApiTimeout("remove", 1000L);
        ServiceClient serviceClient = builder.build();
```
