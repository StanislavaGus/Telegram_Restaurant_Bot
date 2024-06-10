package org.node.service;

import org.node.entity.AcceptableAllergy;
import org.node.repository.AcceptableAllergiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AllergyLoaderService {

    private final AcceptableAllergiesRepository acceptableAllergiesRepository;

    @Autowired
    public AllergyLoaderService(AcceptableAllergiesRepository acceptableAllergiesRepository) {
        this.acceptableAllergiesRepository = acceptableAllergiesRepository;
    }

    public Mono<Void> loadAllergiesFromFile(MultipartFile file) {
        List<AcceptableAllergy> allergies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
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
