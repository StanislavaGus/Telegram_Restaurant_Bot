package org.node.dao;

import org.node.entity.Allergy;
import org.node.entity.Preference;
import org.node.entity.User;
import org.node.entity.Visit;
import org.node.repository.AllergiesRepository;
import org.node.repository.PreferencesRepository;
import org.node.repository.UserRepository;
import org.node.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserDao {
    private final UserRepository userRepository;
    private final PreferencesRepository preferencesRepository;
    private final AllergiesRepository allergiesRepository;
    private final VisitRepository visitRepository;

    @Autowired
    public UserDao(UserRepository userRepository, PreferencesRepository preferencesRepository, AllergiesRepository allergiesRepository, VisitRepository visitRepository) {
        this.userRepository = userRepository;
        this.preferencesRepository = preferencesRepository;
        this.allergiesRepository = allergiesRepository;
        this.visitRepository = visitRepository;
    }

    public Mono<Void> saveUser(User user) {
        return userRepository.save(user).then();
    }

    public Mono<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Flux<String> getAllUserNames() {
        return userRepository.findAllUsernames();
    }

    public Mono<Void> saveUserPreference(Long userId, String preference) {
        Preference newPreference = new Preference();
        newPreference.setUserId(userId);
        newPreference.setPreference(preference);
        return preferencesRepository.save(newPreference).then();
    }

    public Flux<String> findPreferencesByUserId(Long userId) {
        return preferencesRepository.findByUserId(userId)
                .map(Preference::getPreference);
    }

    public Mono<Void> deleteUserPreference(Long userId, String preference) {
        return preferencesRepository.findByUserIdAndPreference(userId, preference)
                .flatMap(preferencesRepository::delete)
                .then();
    }

    public Mono<Void> saveUserAllergy(Long userId, String allergy) {
        Allergy newAllergy = new Allergy();
        newAllergy.setUserId(userId);
        newAllergy.setAllergy(allergy);

        return allergiesRepository.save(newAllergy).then();
    }

    public Flux<String> findAllergiesByUserId(Long userId) {
        return allergiesRepository.findByUserId(userId)
                .map(Allergy::getAllergy);
    }

    public Mono<Void> deleteUserAllergy(Long userId, String allergy) {
        return allergiesRepository.deleteByUserIdAndAllergy(userId, allergy);
    }

    public Mono<Void> saveVisit(Long userId, String restaurantId) {
        Visit visit = new Visit(userId, restaurantId, false); // Создаем объект Visit с userId, restaurantId и visited = false
        return visitRepository.save(visit).then();
    }

    public Flux<Visit> findVisitsByUserId(Long userId) {
        return visitRepository.findByUserId(userId);
    }

    public Mono<Void> updateVisitStatus(Long userId, String restaurantId, boolean visited) {
        return visitRepository.updateVisitStatus(userId, restaurantId, visited);
    }

    public Mono<Void> deleteVisit(Long userId, String restaurantId) {
        return visitRepository.deleteVisit(userId, restaurantId);
    }

}