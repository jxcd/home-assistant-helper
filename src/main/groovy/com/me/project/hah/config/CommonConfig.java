package com.me.project.hah.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonConfig {
    @Value("${env.config.rest-connect-timeout:5000}")
    private int restConnectTimeout;
    @Value("${env.config.rest-read-timeout:10000}")
    private int restReadTimeout;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(restConnectTimeout);
        requestFactory.setReadTimeout(restReadTimeout);
        return new RestTemplate(requestFactory);
    }
}
