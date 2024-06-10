package org.node.repository;

import org.node.entity.AcceptableAllergy;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AcceptableAllergiesRepository extends ReactiveCrudRepository<AcceptableAllergy, Long> {
    Flux<AcceptableAllergy> findAll();
}
