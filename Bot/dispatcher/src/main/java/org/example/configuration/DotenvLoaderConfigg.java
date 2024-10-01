package org.example.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Log4j2
public class DotenvLoaderConfigg {

    private final ConfigurableEnvironment environment;

    @Autowired
    public DotenvLoaderConfigg(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadDotenv() {
        Dotenv dotenv = Dotenv.load();
        Map<String, Object> dotenvProperties = new HashMap<>();

        // Загружаем все переменные из .env в Map
        dotenv.entries().forEach(entry -> {
            dotenvProperties.put(entry.getKey(), entry.getValue());
        });

        // Добавляем их в Spring Environment, чтобы они были доступны через ${...}
        environment.getPropertySources().addLast(new MapPropertySource("dotenvProperties", dotenvProperties));
    }
}