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

    public Mono<JsonNode> searchRestaurants(String location) {
        return Mono.fromCallable(() -> {
            String url = String.format("https://api.foursquare.com/v3/places/search?query=restaurant&near=%s&limit=10", location);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiKey)
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
