package com.example.ai;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AIService {
    
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions"; // DeepSeek API示例
    private static final String API_KEY = "your-api-key-here"; // 替換為你的API密鑰
    
    /**
     * 調用AI生成思維導圖
     */
    public static String generateMindMap(String prompt) {
        // TODO: 替換為實際的API調用
        // 這裡先返回模擬數據用於測試
        
        return simulateMindMapData(prompt);
        
        /* 實際API調用示例：
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);
            
            String jsonInput = String.format(
                "{\"model\":\"deepseek-chat\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"temperature\":0.7}",
                prompt
            );
            
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return parseAIResponse(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultMindMap();
        }
        */
    }
    
    /**
     * 模擬AI返回的思維導圖數據（用於測試）
     */
    private static String simulateMindMapData(String prompt) {
        // 根據不同主題返回不同的模擬數據
        if (prompt.contains("Java") || prompt.contains("編程")) {
            return "{\"centralTopic\":\"Java編程\",\"children\":[" +
                   "{\"title\":\"基礎語法\",\"children\":[{\"title\":\"變量\"},{\"title\":\"循環\"},{\"title\":\"條件\"}]}," +
                   "{\"title\":\"面向對象\",\"children\":[{\"title\":\"類\"},{\"title\":\"繼承\"},{\"title\":\"多態\"}]}," +
                   "{\"title\":\"集合框架\",\"children\":[{\"title\":\"List\"},{\"title\":\"Set\"},{\"title\":\"Map\"}]}" +
                   "]}";
        } else if (prompt.contains("項目") || prompt.contains("管理")) {
            return "{\"centralTopic\":\"項目管理\",\"children\":[" +
                   "{\"title\":\"需求分析\",\"children\":[{\"title\":\"用戶調研\"},{\"title\":\"需求文檔\"}]}," +
                   "{\"title\":\"設計階段\",\"children\":[{\"title\":\"架構設計\"},{\"title\":\"數據庫設計\"}]}," +
                   "{\"title\":\"開發階段\",\"children\":[{\"title\":\"編碼\"},{\"title\":\"測試\"}]}," +
                   "{\"title\":\"部署運維\",\"children\":[{\"title\":\"上線\"},{\"title\":\"監控\"}]}" +
                   "]}";
        } else {
            return "{\"centralTopic\":\"" + prompt + "\",\"children\":[" +
                   "{\"title\":\"分支1\",\"children\":[{\"title\":\"子項1-1\"},{\"title\":\"子項1-2\"}]}," +
                   "{\"title\":\"分支2\",\"children\":[{\"title\":\"子項2-1\"},{\"title\":\"子項2-2\"}]}," +
                   "{\"title\":\"分支3\",\"children\":[{\"title\":\"子項3-1\"},{\"title\":\"子項3-2\"}]}" +
                   "]}";
        }
    }
    
    /**
     * 解析AI響應
     */
    private static String parseAIResponse(String response) {
        // TODO: 解析實際API返回的JSON
        return response;
    }
    
    /**
     * 獲取默認思維導圖
     */
    private static String getDefaultMindMap() {
        return "{\"centralTopic\":\"默認思維導圖\",\"children\":[" +
               "{\"title\":\"節點1\",\"children\":[{\"title\":\"子節點1-1\"}]}," +
               "{\"title\":\"節點2\",\"children\":[{\"title\":\"子節點2-1\"}]}" +
               "]}";
    }
}