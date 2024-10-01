package org.node.service;

import lombok.extern.log4j.Log4j2;
import org.node.entity.AvailablePreference;
import org.node.repository.AvailablePreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class PreferencesLoaderService {

    private final AvailablePreferencesRepository availablePreferencesRepository;
    private final DatabaseClient databaseClient;

    @Autowired
    public PreferencesLoaderService(AvailablePreferencesRepository availablePreferencesRepository, DatabaseClient databaseClient) {
        this.availablePreferencesRepository = availablePreferencesRepository;
        this.databaseClient = databaseClient;
    }

    @PostConstruct
    public void init() {
        recreateTable()
                .then(loadPreferencesFromFile("/preferences.txt"))
                .doOnSuccess(aVoid -> log.info("Preferences loaded successfully"))
                .doOnError(throwable -> log.error("Failed to load preferences: " + throwable.getMessage()))
                .subscribe();
    }

    private Mono<Void> recreateTable() {
        return databaseClient.sql("DROP TABLE IF EXISTS available_preferences")
                .then()
                .then(databaseClient.sql("CREATE TABLE available_preferences (id SERIAL PRIMARY KEY, preference VARCHAR(255))").then());
    }

    public Mono<Void> loadPreferencesFromFile(String filePath) {
        List<AvailablePreference> preferences = new ArrayList<>();
        try (InputStream inputStream = getClass().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                AvailablePreference preference = new AvailablePreference();
                preference.setPreference(line);
                preferences.add(preference);
            }
        } catch (IOException e) {
            return Mono.error(e);
        }
        return availablePreferencesRepository.saveAll(preferences).then();
    }
}
