package org.node.repository;

import org.node.entity.AvailablePreference;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AvailablePreferencesRepository extends ReactiveCrudRepository<AvailablePreference, Long> {
    Flux<AvailablePreference> findAll();
    @Query("SELECT * FROM available_preferences ORDER BY RANDOM() LIMIT :limit")
    Flux<AvailablePreference> findRandomPreferences(int limit);
}
