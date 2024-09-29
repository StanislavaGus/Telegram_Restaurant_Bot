package org.node.repository;

import org.node.entity.Preference;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PreferencesRepository extends ReactiveCrudRepository<Preference, Long> {
    Flux<Preference> findByUserId(Long userId);
    Flux<Preference> findByUserIdAndPreference(Long userId, String preference);
}
