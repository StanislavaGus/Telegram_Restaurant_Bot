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

    public Mono<JsonNode> searchRestaurants(String location, String cuisine, String keywords) {
        return Mono.fromCallable(() -> {
            StringBuilder urlBuilder = new StringBuilder("https://api.foursquare.com/v3/places/search?query=restaurant&near=")
                    .append(location);
            if (cuisine != null && !cuisine.isEmpty()) {
                urlBuilder.append("&categories=").append(cuisine);
            }
            if (keywords != null && !keywords.isEmpty()) {
                urlBuilder.append("&keywords=").append(keywords);
            }
            urlBuilder.append("&limit=10");

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
}
