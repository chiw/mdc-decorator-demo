mdc-decorator-demo

A simple demo to configure ```ThreadPoolTaskExecutor``` with usage ```TaskDecorator``` to copy MDC context to downstream thread

How to use:
```
mvn spring-boot:run
```

Case 1 (negative case) - MDC context cannot be passed to downstream thread
```$xslt
http://localhost:8081/api/hello-world?name=def
```

```$xslt
21-11-2018 04:50:17.173  [app:hello-world-ms custom-mdc-field:hello-world] 30404 [http-nio-8081-exec-1] INFO  com.example.rest.HelloWorldController.helloWorld - HelloWorldResource.helloWorld(name:abc)
21-11-2018 04:50:17.182  [app:hello-world-ms custom-mdc-field:] 30404 [MvcAsync1] INFO  com.example.service.HelloWorldService.sayHello - HelloWorldService.sayHello(name:abc)
```

Case 2 - ```MDC``` context can be passed to downstream ```@Async``` thread
```$xslt
http://localhost:8081/api/hello-world-async?name=abc
```

```$xslt
21-11-2018 04:51:38.101  [app:hello-world-ms custom-mdc-field:hello-world-async] 30404 [http-nio-8081-exec-4] INFO  com.example.rest.HelloWorldController.helloWorldAsync - HelloWorldResource.helloWorldAsync(name:abc)
21-11-2018 04:51:38.105  [app:hello-world-ms custom-mdc-field:hello-world-async] 30404 [asyncExecutor-1] INFO  com.example.service.HelloWorldService.asyncSayHello - HelloWorldService.asyncSayHello(name:abc)

```

Case 3: ```MDC``` context can be passed to downstream thread via executor with ```MdcTaskDecorator```
```$xslt
http://localhost:8081/api/hello-world-with-executor?name=abc
```

```$xslt
21-11-2018 04:52:03.163  [app:hello-world-ms custom-mdc-field:hello-world-with-executor] 30404 [http-nio-8081-exec-5] INFO  com.example.rest.HelloWorldController.helloWorldWithExecutor - HelloWorldResource.helloWorldWithExecutor(name:abc)
21-11-2018 04:52:03.167  [app:hello-world-ms custom-mdc-field:hello-world-with-executor] 30404 [asyncExecutor-1] INFO  com.example.service.HelloWorldService.sayHello - HelloWorldService.sayHello(name:abc)
```


Reference links:

https://stackoverflow.com/questions/6073019/how-to-use-mdc-with-thread-pools
https://moelholm.com/2017/07/24/spring-4-3-using-a-taskdecorator-to-copy-mdc-data-to-async-threads/
https://github.com/moelholm/smallexamples/tree/master/spring43-async-taskdecorator