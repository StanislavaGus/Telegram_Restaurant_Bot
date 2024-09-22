package org.node.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class FoursquareService {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;

    @Autowired
    public FoursquareService(OkHttpClient client, @Value("${foursquare.api.key}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    public Mono<JsonNode> searchRestaurants(String location, String keywords, String sort, Boolean openNow, Integer maxPrice, Double latitude, Double longitude) {
        return Mono.fromCallable(() -> {
            StringBuilder urlBuilder = new StringBuilder("https://api.foursquare.com/v3/places/search?query=");

            if (keywords != null && !keywords.isEmpty()) {
                urlBuilder.append(keywords);
            } else {
                urlBuilder.append("restaurant"); // Если ключевые слова не заданы, используем стандартное значение
            }

            if (location != null && !location.isEmpty()) {
                urlBuilder.append("&near=").append(location);
            }

            if (openNow != null) {
                urlBuilder.append("&open_now=").append(openNow);
            }

            if (sort != null && !sort.isEmpty()) {
                urlBuilder.append("&sort=").append(sort);
            }

            urlBuilder.append("&min_price=1"); // Минимальная цена зафиксирована

            if (maxPrice != null) {
                urlBuilder.append("&max_price=").append(maxPrice);
            }

            if (latitude != null && longitude != null) {
                urlBuilder.append("&ll=").append(latitude).append(",").append(longitude);
            }

            urlBuilder.append("&limit=5");

            // Добавляем авторизационный ключ и выполняем запрос
            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .addHeader("Authorization", apiKey)
                    .addHeader("accept", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                return objectMapper.readTree(response.body().string());
            }
        });
    }


    public Mono<JsonNode> searchRandomRestaurant(String location, String radius) {
        return Mono.fromCallable(() -> {
            StringBuilder urlBuilder = new StringBuilder("https://api.foursquare.com/v3/places/search?query=restaurant&near=")
                    .append(location)
                    .append("&area=").append(radius)
                    .append("&limit=1");

            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .addHeader("Authorization", apiKey)
                    .addHeader("accept", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                return objectMapper.readTree(response.body().string());
            }
        });
    }

    public String getRestaurantLink(String fsqId) {
        String url = "https://api.foursquare.com/v3/places/" + fsqId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", apiKey)
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return jsonNode.get("link").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
