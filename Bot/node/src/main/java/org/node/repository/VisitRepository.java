package org.node.repository;

import org.node.entity.Visit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VisitRepository extends ReactiveCrudRepository<Visit, Long> {
    Flux<Visit> findByUserId(Long userId);
    @Query("UPDATE visits SET visited = :visited WHERE user_id = :userId AND restaurant_id = :restaurantId")
    Mono<Void> updateVisitStatus(Long userId, String restaurantId, boolean visited);
    @Query("DELETE FROM visits WHERE user_id = :userId AND restaurant_id = :restaurantId")
    Mono<Void> deleteVisit(Long userId, String restaurantId);
}
