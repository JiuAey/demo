package com.example.demo.weatherassistant.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "uapis")
public class UapisConfig {
    private String apiKey;
    private String baseUrl = "https://uapis.cn/api";
    private String weatherPath = "/misc/weather";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}