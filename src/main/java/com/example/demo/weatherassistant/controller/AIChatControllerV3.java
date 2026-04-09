package com.example.demo.weatherassistant.controller;


import com.example.demo.weatherassistant.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.ChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIChatControllerV3 {

    private final ChatModel chatModel;
    private final WeatherService weatherService;

    @PostMapping("/v3/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        log.info("用户提问: {}", userMessage);

        // 1. 提取或询问城市
        String extractedCity = extractCity(userMessage);
        String finalResponse;

        if (extractedCity != null) {
            // 2. 查询真实天气
            try {
                String weatherInfo = weatherService.queryRealWeather(extractedCity);

                // 3. 让AI根据天气信息组织友好回答
                ChatClient client = ChatClient.builder(chatModel).build();
                String aiAnswer = client.prompt()
                        .system("你是一个专业的天气助手，请根据用户提供的【天气信息】来回答用户的原始问题。回答要友好、贴近生活，可以给出穿衣、出行等建议。如果天气信息是模拟的，请注明‘基于模拟数据’。")
                        .user("【用户原始问题】" + userMessage + "\n【查询到的天气信息】" + weatherInfo)
                        .call()
                        .content();
                finalResponse = aiAnswer;

            } catch (Exception e) {
                log.error("天气查询失败", e);
                finalResponse = "抱歉，查询天气时遇到了问题: " + e.getMessage();
            }
        } else {
            // 4. 如果没提到城市，让AI引导用户输入
            ChatClient client = ChatClient.builder(chatModel).build();
            finalResponse = client.prompt()
                    .system("你是一个天气助手。用户的问题中没有指明具体城市。请用中文友好地引导用户说出他想查询的城市，例如‘您想了解哪个城市的天气呢？’")
                    .user(userMessage)
                    .call()
                    .content();
        }

        log.info("AI回复: {}", finalResponse);
        return finalResponse;
    }

    // 简单的城市提取逻辑（可替换为更复杂的NLP或AI提取）
    private String extractCity(String message) {
        String[] cities = {"北京", "上海", "广州", "深圳"};
        for (String city : cities) {
            if (message.contains(city)) {
                return city;
            }
        }
        return null;
    }
}