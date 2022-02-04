package com.test.okhttptest;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OkhttpTestApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OkhttpTestApplication.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
