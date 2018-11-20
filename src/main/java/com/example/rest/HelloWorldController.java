package com.example.rest;

import com.example.service.HelloWorldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldController.class);

    private static final String CUSTOM_MDC_FIELD_KEY = "custom-mdc-field";

    Executor asyncExecutor;

    private HelloWorldService helloWorldService;

    @Autowired
    public HelloWorldController(@Qualifier("asyncExecutor") Executor asyncExecutor,
                                HelloWorldService helloWorldService) {
        this.asyncExecutor = asyncExecutor;
        this.helloWorldService = helloWorldService;
    }

    /**
     * Case 1: (Negative case) MDC context cannot be passed to downstream thread
     */
    @GetMapping(value = "/hello-world")
    Callable<ResponseEntity<String>> helloWorld(@RequestParam("name") String name) throws Exception {
        MDC.put(CUSTOM_MDC_FIELD_KEY, "hello-world");

        LOG.info("HelloWorldResource.helloWorld(name:{})", name);

        return new Callable<ResponseEntity<String>>() {
            @Override
            public ResponseEntity<String> call() throws Exception {
                return ResponseEntity.ok().body(helloWorldService.sayHello(name));
            }
        };
    }

    /**
     * Case 2: MDC context can be passed to downstream @Async thread
     */
    @GetMapping(value = "/hello-world-async")
    ResponseEntity<String> helloWorldAsync(@RequestParam("name") String name) throws Exception {
        MDC.put(CUSTOM_MDC_FIELD_KEY, "hello-world-async");

        LOG.info("HelloWorldResource.helloWorldAsync(name:{})", name);

        helloWorldService.asyncSayHello(name);
        return ResponseEntity.ok().body("processed request asynchornously");
    }

    /**
     * Case 3: MDC context can be passed to downstream thread via executor with MdcTaskDecorator
     */
    @GetMapping(value = "/hello-world-with-executor")
    DeferredResult<ResponseEntity<String>> helloWorldWithExecutor(@RequestParam("name") String name) throws Exception {
        MDC.put(CUSTOM_MDC_FIELD_KEY, "hello-world-with-executor");

        LOG.info("HelloWorldResource.helloWorldWithExecutor(name:{})", name);

        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>();

        Future<String> completableFuture =  CompletableFuture.supplyAsync(() -> {
            return helloWorldService.sayHello(name);
        }, asyncExecutor);

        deferredResult.setResult(ResponseEntity.ok().body(completableFuture.get()));
        return deferredResult;
    }
}
