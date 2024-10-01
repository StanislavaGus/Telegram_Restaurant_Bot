package org.node.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvLoaderConfig {

    private final ConfigurableEnvironment environment;

    @Autowired
    public DotenvLoaderConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadDotenv() {
        Dotenv dotenv = Dotenv.load();
        Map<String, Object> dotenvProperties = new HashMap<>();

        dotenv.entries().forEach(entry -> dotenvProperties.put(entry.getKey(), entry.getValue()));

        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvProperties));
    }
}
