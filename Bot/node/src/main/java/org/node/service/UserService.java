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
    private final AvailablePreferencesRepository availablePreferencesRepository;
    private final AllergiesRepository allergiesRepository;
    private final PreferencesRepository preferencesRepository;
    private final AcceptableAllergiesRepository acceptableAllergiesRepository;
    private final PasswordEncoder passwordEncoder;
    private final FoursquareService foursquareService;

    @Autowired
    public UserService(UserDao userDao, AvailablePreferencesRepository availablePreferencesRepository,
                       AllergiesRepository allergiesRepository, PreferencesRepository preferencesRepository, AcceptableAllergiesRepository acceptableAllergiesRepository, PasswordEncoder passwordEncoder, FoursquareService foursquareService) {
        this.userDao = userDao;
        this.availablePreferencesRepository = availablePreferencesRepository;
        this.allergiesRepository = allergiesRepository;
        this.preferencesRepository = preferencesRepository;
        this.acceptableAllergiesRepository = acceptableAllergiesRepository;
        this.passwordEncoder = passwordEncoder;
        this.foursquareService = foursquareService;
    }

    public Mono<JsonNode> findRestaurant(String location, String keywords, String sort, Boolean openNow, Integer maxPrice, Double latitude, Double longitude) {
        return foursquareService.searchRestaurants(location, keywords, sort, openNow, maxPrice, latitude, longitude);
    }

    public Mono<JsonNode> requestRandomRestaurant(String location, String area) {
        return foursquareService.searchRandomRestaurant(location, area);
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
        return preferencesRepository.findByUserId(userId)
                .filter(existingPreference -> existingPreference.getPreference().equalsIgnoreCase(preference))
                .hasElements()
                .flatMap(isAlreadyAdded -> {
                    if (isAlreadyAdded) {
                        return Mono.error(new IllegalArgumentException("Preference already added for user"));
                    } else {
                        Preference newPreference = new Preference();
                        newPreference.setUserId(userId);
                        newPreference.setPreference(preference);
                        return preferencesRepository.save(newPreference).then();
                    }
                });
    }



    public Flux<String> getUserPreferences(Long userId) {
        return preferencesRepository.findByUserId(userId)
                .map(Preference::getPreference);
    }


    public Mono<Void> deleteUserPreference(Long userId, String preference) {
        return userDao.deleteUserPreference(userId, preference);
    }

    public Mono<Void> addUserAllergy(Long userId, String allergy) {
        return allergiesRepository.findByUserId(userId)
                .filter(existingAllergy -> existingAllergy.getAllergy().equalsIgnoreCase(allergy))
                .hasElements()
                .flatMap(isAlreadyAdded -> {
                    if (isAlreadyAdded) {
                        return Mono.error(new IllegalArgumentException("Allergy already added for user"));
                    } else {
                        Allergy newAllergy = new Allergy();
                        newAllergy.setUserId(userId);
                        newAllergy.setAllergy(allergy); // Сохраняем аллергию как есть
                        return allergiesRepository.save(newAllergy).then();
                    }
                });
    }



    public Flux<String> getUserAllergies(Long userId) {
        return allergiesRepository.findByUserId(userId)
                .map(Allergy::getAllergy);
    }

    public Mono<Void> deleteUserAllergy(Long userId, String allergy) {
        return allergiesRepository.findByUserId(userId)
                .filter(existingAllergy -> existingAllergy.getAllergy().equalsIgnoreCase(allergy))
                .singleOrEmpty()
                .flatMap(existingAllergy ->
                        allergiesRepository.delete(existingAllergy)
                                .then(Mono.just("Allergy deleted successfully!"))
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Allergy not found for user")))
                .then();
    }

    public Mono<Boolean> isAcceptableAllergy(String allergy) {
        return acceptableAllergiesRepository.findAll()
                .filter(acceptableAllergy -> acceptableAllergy.getAllergy().equalsIgnoreCase(allergy))
                .hasElements();
    }

    public Mono<Void> addVisit(Long userId, String restaurantId) {
        return userDao.saveVisit(userId, restaurantId);
    }

    public Flux<Visit> getVisitList(Long userId) {
        return userDao.findVisitsByUserId(userId);
    }


    public Mono<Boolean> markVisited(Long userId, String restaurantId) {
        return userDao.findVisitsByUserId(userId)
                .filter(visit -> visit.getRestaurantId().equals(restaurantId))  // убедитесь, что visit — это объект с полем restaurantId
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
