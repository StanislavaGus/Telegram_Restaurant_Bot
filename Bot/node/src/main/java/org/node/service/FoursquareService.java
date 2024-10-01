package org.node.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

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

            if (StringUtils.isNotEmpty(keywords)) {
                urlBuilder.append(keywords);
            } else {
                urlBuilder.append("restaurant");
            }

            if (StringUtils.isNotEmpty(location)) {
                urlBuilder.append("&near=").append(location);
            }

            if (Objects.nonNull(openNow)) {
                urlBuilder.append("&open_now=").append(openNow);
            }

            if (StringUtils.isNotEmpty(sort)) {
                urlBuilder.append("&sort=").append(sort);
            }

            urlBuilder.append("&min_price=1");

            if (Objects.nonNull(maxPrice)) {
                urlBuilder.append("&max_price=").append(maxPrice);
            }

            if ( Objects.nonNull(latitude)&& Objects.nonNull(longitude)) {
                urlBuilder.append("&ll=").append(latitude).append(",").append(longitude);
            }

            urlBuilder.append("&limit=50");

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
