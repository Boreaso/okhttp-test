package com.test.okhttptest.controller;

import com.test.okhttptest.service.OkHttpRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/api")
public class Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private final ThreadPoolExecutor threadPoolExecutor;
    private final OkHttpRequestService okHttpRequestService;

    @Value("${okhttp.request.async}")
    private boolean isUseAsync;

    public Controller(OkHttpRequestService okHttpRequestService) {
        this.threadPoolExecutor = new ThreadPoolExecutor(100, 1000,
                60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        this.okHttpRequestService = okHttpRequestService;
        LOGGER.info("ThreadPool config: corePoolSize={}, maxPoolSize={}", threadPoolExecutor.getCorePoolSize(),
                threadPoolExecutor.getMaximumPoolSize());
    }

    @GetMapping("/okhttp")
    public Mono<String> okhttp() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            String result = isUseAsync ? okHttpRequestService.executeAsync()
                    : okHttpRequestService.execute();
            LOGGER.info("ThreadPool queued tasks: {}", threadPoolExecutor.getQueue().size());
            return result;
        }, threadPoolExecutor);
        return Mono.fromFuture(completableFuture);
    }
}
