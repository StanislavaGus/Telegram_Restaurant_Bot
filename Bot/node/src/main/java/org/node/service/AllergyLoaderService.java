package org.node.service;

import lombok.extern.log4j.Log4j2;
import org.node.entity.AcceptableAllergy;
import org.node.repository.AcceptableAllergiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class AllergyLoaderService {

    private final AcceptableAllergiesRepository acceptableAllergiesRepository;
    private final DatabaseClient databaseClient;

    @Autowired
    public AllergyLoaderService(AcceptableAllergiesRepository acceptableAllergiesRepository, DatabaseClient databaseClient) {
        this.acceptableAllergiesRepository = acceptableAllergiesRepository;
        this.databaseClient = databaseClient;
    }

    @PostConstruct
    public void init() {
        recreateTable()
                .then(loadAllergiesFromFile("/allergies.txt"))
                .doOnSuccess(aVoid -> log.info("Allergies loaded successfully"))
                .doOnError(throwable -> log.error("Error loading allergies: " + throwable.getMessage()))
                .subscribe();
    }

    private Mono<Void> recreateTable() {
        return databaseClient.sql("DROP TABLE IF EXISTS acceptable_allergies")
                .then()
                .then(databaseClient.sql("CREATE TABLE acceptable_allergies (id SERIAL PRIMARY KEY, allergy VARCHAR(255))").then());
    }

    public Mono<Void> loadAllergiesFromFile(String filePath) {
        List<AcceptableAllergy> allergies = new ArrayList<>();
        try (InputStream inputStream = getClass().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                AcceptableAllergy allergy = new AcceptableAllergy();
                allergy.setAllergy(line);
                allergies.add(allergy);
            }
        } catch (IOException e) {
            return Mono.error(e);
        }
        return acceptableAllergiesRepository.saveAll(allergies).then();
    }
}
