package com.penguin.linknote.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 只對專案自己的 REST Controller 加上 /api 前綴，避免污染 springdoc 內建的 OpenAPI endpoint
        configurer.addPathPrefix("/api",
            c -> c.isAnnotationPresent(RestController.class)
                 && !c.getPackageName().startsWith("org.springdoc"));
    }
}
