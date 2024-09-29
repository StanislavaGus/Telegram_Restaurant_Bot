package org.node.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.node.dao.UserDao;
import org.node.entity.Allergy;
import org.node.entity.Preference;
import org.node.entity.User;
import org.node.entity.Visit;
import org.node.repository.AllergiesRepository;
import org.node.repository.AcceptableAllergiesRepository;
import org.node.repository.AvailablePreferencesRepository;
import org.node.repository.PreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final FoursquareService foursquareService;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder, FoursquareService foursquareService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.foursquareService = foursquareService;
    }

    public Mono<Void> addUser(String username, String password, String email) {
        return userDao.findUserByEmail(email)
                .flatMap(existingUser -> Mono.error(new IllegalArgumentException("User with such an email already exists!")))
                .switchIfEmpty(userDao.findUserByUsername(username)
                        .flatMap(existingUser -> Mono.error(new IllegalArgumentException("User with such a username already exists!")))
                        .switchIfEmpty(userDao.saveUser(new User(null, username, passwordEncoder.encode(password), email))) // Создаем новый объект User
                )
                .then();
    }


    public Mono<Boolean> authenticate(String username, String password) {
        return userDao.findUserByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Mono<Long> getUserIdByUsername(String username) {
        return userDao.findUserByUsername(username)
                .flatMap(user -> Mono.just(user.getId()));
    }

    public Flux<String> getAllUsers() {
        return userDao.getAllUserNames();
    }

    public Mono<Void> addUserPreference(Long userId, String preference) {
        return userDao.findPreferencesByUserId(userId)
                .filter(existingPreference -> existingPreference.equalsIgnoreCase(preference))
                .hasElements()
                .flatMap(isAlreadyAdded -> {
                    if (isAlreadyAdded) {
                        return Mono.error(new IllegalArgumentException("Preference already added for user"));
                    } else {
                        return userDao.saveUserPreference(userId, preference);
                    }
                });
    }

    public Flux<String> getUserPreferences(Long userId) {
        return userDao.findPreferencesByUserId(userId);
    }

    public Mono<Void> deleteUserPreference(Long userId, String preference) {
        return userDao.deleteUserPreference(userId, preference);
    }

    public Mono<Void> addUserAllergy(Long userId, String allergy) {
        return userDao.findAllergiesByUserId(userId)
                .filter(existingAllergy -> existingAllergy.equalsIgnoreCase(allergy))
                .hasElements()
                .flatMap(isAlreadyAdded -> {
                    if (isAlreadyAdded) {
                        return Mono.error(new IllegalArgumentException("Allergy already added for user"));
                    } else {
                        return userDao.saveUserAllergy(userId, allergy);
                    }
                });
    }

    public Flux<String> getUserAllergies(Long userId) {
        return userDao.findAllergiesByUserId(userId);
    }

    public Mono<String> deleteUserAllergy(Long userId, String allergy) {
        return userDao.findAllergiesByUserId(userId)
                .filter(existingAllergy -> existingAllergy.equalsIgnoreCase(allergy))
                .singleOrEmpty()
                .flatMap(existingAllergy ->
                        userDao.deleteUserAllergy(userId, allergy)
                                .thenReturn("Allergy deleted successfully!")
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Allergy not found for user")));
    }

    public Mono<Void> addVisit(Long userId, String restaurantId) {
        return userDao.saveVisit(userId, restaurantId);
    }

    public Flux<Visit> getVisitList(Long userId) {
        return userDao.findVisitsByUserId(userId);
    }

    public Mono<Boolean> markVisited(Long userId, String restaurantId) {
        return userDao.findVisitsByUserId(userId)
                .filter(visit -> visit.getRestaurantId().equals(restaurantId))
                .hasElements()
                .flatMap(exists -> {
                    if (exists) {
                        return userDao.updateVisitStatus(userId, restaurantId, true)
                                .thenReturn(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Mono<Void> removeVisit(Long userId, String restaurantId) {
        return userDao.deleteVisit(userId, restaurantId);
    }
}
