package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import org.example.configuration.BotConfiguration;
import org.example.controller.UpdateController;
import org.node.service.FoursquareService;
import org.node.service.SessionService;
import org.node.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration config;
    private final AnnotationConfigApplicationContext context;
    private final UpdateController updateController;
    private final UserService userService;
    private final SessionService sessionService;
    private final FoursquareService foursquareService;

    @Autowired
    public Bot(BotConfiguration botConfig, AnnotationConfigApplicationContext context, UpdateController updateController, UserService userService, SessionService sessionService, FoursquareService foursquareService) {
        this.config = botConfig;
        this.context = context;
        this.updateController = updateController;
        this.userService = userService;
        this.sessionService = sessionService;
        this.foursquareService = foursquareService;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMainMenu(chatId);
            } else if (messageText.startsWith("/register")) {
                handleRegisterCommand(chatId, messageText);
            } else if (messageText.startsWith("/login")) {
                handleLoginCommand(chatId, messageText);
            } else if (messageText.startsWith("/logout")) {
                handleLogoutCommand(chatId);
            } else if (messageText.startsWith("/addpref")) {
                handleAddPreferenceCommand(chatId, messageText);
            } else if (messageText.startsWith("/viewprefs")) {
                handleViewPreferencesCommand(chatId);
            } else if (messageText.startsWith("/delpref")) {
                handleDeletePreferenceCommand(chatId, messageText);
            } else if (messageText.startsWith("/addallergy")) {
                handleAddAllergyCommand(chatId, messageText);
            } else if (messageText.startsWith("/viewallergies")) {
                handleViewAllergiesCommand(chatId);
            } else if (messageText.startsWith("/delallergy")) {
                handleDeleteAllergyCommand(chatId, messageText);
            } else if (messageText.startsWith("/findrestaurant")) {
                handleFindRestaurantCommand(chatId, messageText);
            } else if (messageText.startsWith("/randomrestaurant")) {
                handleRandomRestaurantCommand(chatId, messageText);
            } else {
                updateController.processUpdate(update);
            }
        }
    }

    private void handleFindRestaurantCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 4);
        if (parts.length >= 2) {
            String location = parts[1];
            String cuisine = parts.length >= 3 ? parts[2] : "";
            String keywords = parts.length == 4 ? parts[3] : "";

            foursquareService.searchRestaurants(location, cuisine, keywords)
                    .doOnSuccess(response -> {
                        StringBuilder responseMessage = new StringBuilder("Restaurants found:\n");
                        JsonNode results = response.get("results");
                        if (results.isArray() && results.size() > 0) {
                            for (JsonNode restaurant : results) {
                                String name = restaurant.get("name").asText();
                                String address = restaurant.get("location").get("formatted_address").asText();
                                responseMessage.append(name).append(", ").append(address).append("\n");
                            }
                        } else {
                            responseMessage.append("No restaurants found.");
                        }
                        sendMessage(chatId, responseMessage.toString());
                    })
                    .doOnError(throwable -> {
                        log.error("Failed to find restaurant", throwable);
                        sendMessage(chatId, "Failed to find restaurant: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessageWithMarkdown(chatId, "Invalid format. Use: /findrestaurant location [categoryID] [keywords]\n\nWhere the category is specified in the format of the number 1300-13392.\n\nThe breakdown of the categories can be viewed on the [website](https://docs.foursquare.com/data-products/docs/categories).");
        }
    }

    private void sendMessageWithMarkdown(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);
        sendAnswerMessage(message);
    }


    private void handleRandomRestaurantCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 3);
        if (parts.length == 3) {
            String location = parts[1];
            String radius = parts[2];

            foursquareService.searchRandomRestaurant(location, radius)
                    .doOnSuccess(response -> {
                        StringBuilder responseMessage = new StringBuilder("Random restaurant:\n");
                        JsonNode results = response.get("results");
                        if (results.isArray() && results.size() > 0) {
                            JsonNode restaurant = results.get(0);
                            String name = restaurant.get("name").asText();
                            String address = restaurant.get("location").get("formatted_address").asText();
                            responseMessage.append(name).append(", ").append(address).append("\n");
                        } else {
                            responseMessage.append("No restaurant found.");
                        }
                        sendMessage(chatId, responseMessage.toString());
                    })
                    .doOnError(throwable -> {
                        log.error("Failed to find random restaurant", throwable);
                        sendMessage(chatId, "Failed to find random restaurant: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /randomrestaurant location radius");
        }
    }

    private void handleAddAllergyCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String allergy = parts[1];
            Long userId = sessionService.getUserId(chatId);

            userService.addUserAllergy(userId, allergy)
                    .doOnSuccess(aVoid -> sendMessage(chatId, "Allergy added successfully!"))
                    .doOnError(throwable -> {
                        log.error("Failed to add allergy", throwable);
                        sendMessage(chatId, "Failed to add allergy: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /addallergy allergy");
        }
    }



    private void handleViewAllergiesCommand(long chatId) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        Long userId = sessionService.getUserId(chatId);

        userService.getUserAllergies(userId)
                .collectList()
                .doOnSuccess(allergies -> {
                    if (allergies.isEmpty()) {
                        sendMessage(chatId, "You have no allergies.");
                    } else {
                        sendMessage(chatId, "Your allergies: " + String.join(", ", allergies));
                    }
                })
                .doOnError(throwable -> {
                    log.error("Failed to fetch allergies", throwable);
                    sendMessage(chatId, "Failed to fetch allergies: " + throwable.getMessage());
                })
                .subscribe();
    }

    private void handleDeleteAllergyCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String allergy = parts[1];
            Long userId = sessionService.getUserId(chatId);

            userService.deleteUserAllergy(userId, allergy)
                    .then(Mono.just("Allergy deleted successfully!"))
                    .doOnSuccess(response -> sendMessage(chatId, response))
                    .doOnError(throwable -> {
                        log.error("Failed to delete allergy", throwable);
                        sendMessage(chatId, "Failed to delete allergy: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /delallergy allergy");
        }
    }



    private void handleAddPreferenceCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String preference = parts[1];
            Long userId = sessionService.getUserId(chatId);

            userService.addUserPreference(userId, preference)
                    .doOnSuccess(aVoid -> sendMessage(chatId, "Preference added successfully!"))
                    .doOnError(throwable -> {
                        log.error("Failed to add preference", throwable);
                        sendMessage(chatId, "Failed to add preference: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /addpref preference");
        }
    }

    private void handleViewPreferencesCommand(long chatId) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        Long userId = sessionService.getUserId(chatId);

        userService.getUserPreferences(userId)
                .collectList()
                .doOnSuccess(preferences -> {
                    if (preferences.isEmpty()) {
                        sendMessage(chatId, "You have no preferences.");
                    } else {
                        sendMessage(chatId, "Your preferences: " + String.join(", ", preferences));
                    }
                })
                .doOnError(throwable -> {
                    log.error("Failed to fetch preferences", throwable);
                    sendMessage(chatId, "Failed to fetch preferences: " + throwable.getMessage());
                })
                .subscribe();
    }

    private void handleDeletePreferenceCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String preference = parts[1];
            Long userId = sessionService.getUserId(chatId);

            userService.getUserPreferences(userId)
                    .collectList()
                    .flatMap(preferences -> {
                        if (preferences.contains(preference)) {
                            return userService.deleteUserPreference(userId, preference)
                                    .then(Mono.just("Preference deleted successfully!"));
                        } else {
                            return Mono.just("This preference does not exist in your list.");
                        }
                    })
                    .doOnNext(response -> sendMessage(chatId, response))
                    .doOnError(throwable -> {
                        log.error("Failed to delete preference", throwable);
                        sendMessage(chatId, "Failed to delete preference: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /delpref preference");
        }
    }


    private void sendMainMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Welcome! Please choose an option:");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("/register"));
        row.add(new KeyboardButton("/login"));
        row.add(new KeyboardButton("/logout"));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        sendAnswerMessage(message);
    }

    private void handleRegisterCommand(long chatId, String messageText) {
        String[] parts = messageText.split(" ");
        if (parts.length == 4) {
            String username = parts[1];
            String password = parts[2];
            String email = parts[3];

            userService.addUser(username, password, email)
                    .doOnSuccess(aVoid -> sendMessage(chatId, "Registration successful!"))
                    .doOnError(throwable -> {
                        log.error("Registration failed", throwable);
                        String errorMessage = throwable.getMessage().contains("User already exists") ?
                                "Registration failed: User already exists" :
                                "Registration failed: " + throwable.getMessage();
                        sendMessage(chatId, errorMessage);
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /register username password email");
        }
    }

    private void handleLoginCommand(long chatId, String messageText) {
        if (sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are already logged in!");
            return;
        }

        String[] parts = messageText.split(" ");
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            userService.authenticate(username, password)
                    .flatMap(authenticated -> {
                        if (authenticated) {
                            return userService.getUserIdByUsername(username)
                                    .doOnSuccess(userId -> sessionService.logInUser(chatId, userId))
                                    .then(Mono.just(authenticated));
                        } else {
                            return Mono.just(authenticated);
                        }
                    })
                    .doOnSuccess(authenticated -> {
                        if (authenticated) {
                            sendMessage(chatId, "Login successful!");
                        } else {
                            sendMessage(chatId, "Login failed: Invalid credentials");
                        }
                    })
                    .doOnError(throwable -> {
                        log.error("Login failed", throwable);
                        String errorMessage = throwable.getMessage().contains("User not found") ?
                                "Login failed: User not found" :
                                "Login failed: " + throwable.getMessage();
                        sendMessage(chatId, errorMessage);
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /login username password");
        }
    }

    private void handleLogoutCommand(long chatId) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        sessionService.logOutUser(chatId);
        sendMessage(chatId, "You have been logged out successfully.");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        sendAnswerMessage(message);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
