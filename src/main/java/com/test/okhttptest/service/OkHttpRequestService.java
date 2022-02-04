package com.test.okhttptest.service;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OkHttpRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpRequestService.class);

    private final OkHttpClient client;

    public OkHttpRequestService(int maxRequests, int maxRequestsPerHost) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxRequests);
        dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
        client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build();
        LOGGER.info("OKHttpClient config: maxRequest={}, maxRequestsPerHost={}",
                maxRequests, maxRequestsPerHost);
    }

    public String execute() {
        Request request = new Request.Builder()
                .get()
                .url("http://124.222.103.67:8888/delayed")
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                return "";
            }
            return body.string();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return "";
    }

    public String executeAsync() {
        Request request = new Request.Builder()
                .get()
                .url("http://124.222.103.67:8888/delayed")
                .build();
        HttpCallback httpCallback = new HttpCallback();
        client.newCall(request).enqueue(httpCallback);
        LOGGER.info("OkHttp async queued calls: {}", client.dispatcher().queuedCallsCount());
        try {
            String result = httpCallback.get(Integer.MAX_VALUE, TimeUnit.SECONDS);
            if (result == null) {
                return "";
            }
            LOGGER.info("OkHttp async remained calls: {}", client.dispatcher().queuedCallsCount());
            return result;
        } catch (ExecutionException | InterruptedException | TimeoutException exception) {
            LOGGER.info("OkHttp async call exception: {}", exception.getMessage());
        }
        return "";
    }

    private static final class HttpCallback extends CompletableFuture<String> implements Callback {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException exception) {
            super.completeExceptionally(exception);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            ResponseBody responseBody = response.body();
            String result = responseBody == null ? "" : responseBody.string();
            super.complete(result);
        }
    }
}
