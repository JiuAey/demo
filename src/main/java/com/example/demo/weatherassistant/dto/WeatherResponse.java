package com.example.demo.weatherassistant.dto;


import lombok.Data;

@Data
public class WeatherResponse {
    //城市
    private String city;
    //温度
    private Double temperature;
    //风速
    private Double windSpeed;
    //天气
   private String weather;
   //湿度
   private Integer humidity;
   //更新时间
   private String updateTime;

   //uapi-sjenvi4go19et3qUB6moMeOrpdQ1LGzaZlJkkAjH


}
