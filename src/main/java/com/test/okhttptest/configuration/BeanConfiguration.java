package com.test.okhttptest.configuration;

import com.test.okhttptest.service.OkHttpRequestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Value("${okhttp.request.max_requests}")
    private int maxRequests;

    @Value("${okhttp.request.max_requests_per_host}")
    private int maxRequestsPerHost;

    @Bean
    public OkHttpRequestService okHttpRequestService() {
        return new OkHttpRequestService(maxRequests, maxRequestsPerHost);
    }
}
