package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class HelloWorldService {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldService.class);

    public String sayHello(String name) {

        LOG.info("HelloWorldService.sayHello(name:{})", name);

        return this.sayHello(name, new Date());
    }

    @Async
    public String asyncSayHello(String name) {

        LOG.info("HelloWorldService.asyncSayHello(name:{})", name);

        return this.sayHello(name, new Date());
    }

    protected String sayHello(String name, Date date) {
        StringBuilder sb = new StringBuilder();

        return sb.append(StringUtils.isEmpty(name) ? "stranger" : name)
                .append(date != null ? date : new Date()).toString();
    }
}
