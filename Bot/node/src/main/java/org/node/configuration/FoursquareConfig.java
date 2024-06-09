package org.node.configuration;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FoursquareConfig {

    @Value("${foursquare.api.key}")
    private String apiKey;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public String foursquareApiKey() {
        return apiKey;
    }
}
