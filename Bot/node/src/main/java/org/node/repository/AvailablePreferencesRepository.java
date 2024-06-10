package org.node.repository;

import org.node.entity.AvailablePreference;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AvailablePreferencesRepository extends ReactiveCrudRepository<AvailablePreference, Long> {
    Flux<AvailablePreference> findAll();
}
