package com.example.demo.weatherassistant.service;

import com.example.demo.weatherassistant.config.UapisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UapisConfig weatherConfig;

    // 城市编码映射（需根据UAPIs文档补充）
    private static final Map<String, String> CITY_CODE_MAP = new HashMap<>();
    static {
        CITY_CODE_MAP.put("北京", "110000");
        CITY_CODE_MAP.put("上海", "310000");
        CITY_CODE_MAP.put("广州", "440100");
        CITY_CODE_MAP.put("深圳", "440300");
    }

    public String queryRealWeather(String city) throws Exception {
        String cityCode = CITY_CODE_MAP.get(city);
        if (cityCode == null) {
            throw new IllegalArgumentException("暂不支持该城市: " + city);
        }

        String url = UriComponentsBuilder.fromHttpUrl(weatherConfig.getBaseUrl() + weatherConfig.getWeatherPath())
                .queryParam("adcode", cityCode)
                .queryParam("lang", "zh")
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + weatherConfig.getApiKey()); // 请根据UAPIs实际要求调整
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // 此处应解析返回的JSON，这里返回原始字符串用于演示
            log.info("UAPIs 原始响应: {}", response.getBody());
            // 模拟返回一个格式化的天气信息
            return String.format("【%s天气模拟】温度：25℃, 天气：晴，湿度：65%%。 (实际数据: %s)",
                    city, response.getBody().substring(0, Math.min(100, response.getBody().length())) + "...");
        } else {
            throw new RuntimeException("天气API请求失败，状态码: " + response.getStatusCode());
        }
    }
}