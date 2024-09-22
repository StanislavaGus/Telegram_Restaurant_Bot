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
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private Boolean choosingCityFlag;
    private Boolean choosingDistanceFlag;

    @Autowired
    public Bot(BotConfiguration botConfig, AnnotationConfigApplicationContext context, UpdateController updateController, UserService userService, SessionService sessionService, FoursquareService foursquareService) {
        this.config = botConfig;
        this.context = context;
        this.updateController = updateController;
        this.userService = userService;
        this.sessionService = sessionService;
        this.foursquareService = foursquareService;
        this.choosingCityFlag = false;
        this.choosingDistanceFlag = false;
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
        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasText() || update.getMessage().hasLocation()) {
                if (update.hasMessage() && update.getMessage().hasLocation()) {
                    removeKeyboard(chatId);
                    handleFindRestaurantCommand(chatId);

                    Location location = update.getMessage().getLocation();

                    // Получаем широту и долготу
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();

                    // Отправляем сообщение с информацией о геолокации
                    sendMessage(chatId, "Your location: " + latitude + ", " + longitude);

                    locationButtonPushed(chatId);
                }
                else if (choosingCityFlag) {
                    sendMessage(chatId, "You Enter - " + messageText);
                    //System.out.println("You Enter - city");

                    //1111111111тут надо его в буфер записать!!!!!!!!1
                    this.choosingCityFlag = false;
                }

                else if (choosingDistanceFlag) {
                    Double distance = Double.parseDouble(messageText);
                    sendMessage(chatId, "You Enter - " + distance);
                    //System.out.println("You Enter - distance");

                    //1111111111тут надо его в буфер записать!!!!!!!!1
                    this.choosingDistanceFlag = false;
                }

                else if (messageText.equals("/start")) {
                    sendMainMenu(chatId);
                } else if (messageText.equals("/help")) {
                    sendHelpMessage(chatId);
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
                    handleFindRestaurantCommand(chatId);

                } else if (messageText.startsWith("Location")) {
                    locationButtonPushed(chatId);
                } else if (messageText.startsWith("Search Filters")) {
                    handleOption2(chatId);
                } else if (messageText.startsWith("Start Searching")) {
                    handleOption3(chatId);

                } else if (messageText.startsWith("Send my location")) {
                    sendMyLocation(chatId);
                } else if (messageText.startsWith("Choose city")) {
                    chooseCity(chatId);
                } else if (messageText.startsWith("Distance")) {
                    chooseDistance(chatId);
                } else if (messageText.startsWith("Go to Searching Menu")) {
                    removeKeyboard(chatId);
                    handleFindRestaurantCommand(chatId);
                } else if (messageText.startsWith("/randomrestaurant")) {
                    handleRandomRestaurantCommand(chatId, messageText);
                } else if (messageText.startsWith("/visitlist")) {
                    handleVisitListCommand(chatId);
                } else if (messageText.startsWith("/addvisit")) {
                    handleAddVisitCommand(chatId, messageText);
                } else if (messageText.startsWith("/showlist")) {
                    handleShowListCommand(chatId);
                } else if (messageText.startsWith("/markvisited")) {
                    handleMarkVisitedCommand(chatId, messageText);
                } else if (messageText.startsWith("/removevisit")) {
                    handleRemoveVisitCommand(chatId, messageText);}


                else {
                    sendHelpSuggestion(chatId);
                }

            } else {
                sendUnsupportedMessageType(chatId);
            }
        }
    }

    private void sendUnsupportedMessageType(long chatId) {
        sendMessage(chatId, "Unsupported file type! Please use /help to see the valid commands.");
    }

    private void sendHelpSuggestion(long chatId) {
        sendMessage(chatId, "Invalid message. Please use /help to see the valid commands.");
    }



    private void sendHelpMessage(long chatId) {
        String helpMessage = "Available commands:\n\n" +
                "/start - Show the main menu\n" +
                "/help - Show this help message\n" +
                "/register [username] [password] [email] - Register a new user\n" +
                "/login [username] [password] - Log in\n" +
                "/logout - Log out\n" +
                "/addpref [preference] - Add a new preference\n" +
                "/viewprefs - View your preferences\n" +
                "/delpref [preference] - Delete a preference\n" +
                "/addallergy [allergy] - Add a new allergy\n" +
                "/viewallergies - View your allergies\n" +
                "/delallergy [allergy] - Delete an allergy\n" +
                "/findrestaurant [location], [categoryID], [keywords], [skipCategories] - Find a restaurant\n" +
                "/randomrestaurant [location] [radius] - Find a random restaurant\n" +
                "/visitlist - Show visit-related commands\n" +
                "/addvisit [restaurant_id] - Add a restaurant to your visit list\n" +
                "/showlist - Show your visit list\n" +
                "/markvisited [restaurant_id] - Mark a restaurant as visited\n" +
                "/removevisit [restaurant_id] - Remove a restaurant from your visit list";

        sendMessage(chatId, helpMessage);
    }

    private void handleVisitListCommand(long chatId) {
        sendMessage(chatId, "Available commands: /addvisit [restaurant_id], /showlist, /markvisited [restaurant_id], /removevisit [restaurant_id]");
    }

    private void handleAddVisitCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String restaurantId = parts[1];
            Long userId = sessionService.getUserId(chatId);

            userService.getVisitList(userId)
                    .collectList()
                    .flatMap(visits -> {
                        boolean restaurantExists = visits.stream().anyMatch(visit -> visit.getRestaurantId().equals(restaurantId));
                        if (restaurantExists) {
                            return Mono.just("Restaurant is already in your visit list.");
                        } else {
                            return userService.addVisit(userId, restaurantId)
                                    .then(Mono.just("Restaurant added to visit list successfully!"));
                        }
                    })
                    .doOnSuccess(message -> sendMessage(chatId, message))
                    .doOnError(throwable -> {
                        log.error("Failed to add restaurant to visit list", throwable);
                        sendMessage(chatId, "Failed to add restaurant to visit list: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /addvisit [restaurant_id]");
        }
    }


    private void handleShowListCommand(long chatId) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        Long userId = sessionService.getUserId(chatId);

        userService.getVisitList(userId)
                .collectList()
                .flatMap(visits -> {
                    if (visits.isEmpty()) {
                        return Mono.just("Your visit list is empty.");
                    } else {
                        StringBuilder response = new StringBuilder("Your visit list:\n\n");
                        visits.forEach(visit -> {
                            if (visit.getVisited()) {
                                response.append("<a href='http://example.com'>").append(visit.getRestaurantId()).append("</a>").append("\n");
                            } else {
                                response.append(visit.getRestaurantId()).append("\n");
                            }
                        });
                        return Mono.just(response.toString());
                    }
                })
                .doOnSuccess(response -> sendMessageWithHTML(chatId, response))
                .doOnError(throwable -> {
                    log.error("Failed to fetch visit list", throwable);
                    sendMessage(chatId, "Failed to fetch visit list: " + throwable.getMessage());
                })
                .subscribe();
    }

    private void sendMessageWithHTML(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableHtml(true);
        sendAnswerMessage(message);
    }

    private void handleMarkVisitedCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String restaurantId = parts[1];
            Long userId = sessionService.getUserId(chatId);

            userService.markVisited(userId, restaurantId)
                    .doOnSuccess(result -> {
                        if (result) {
                            sendMessage(chatId, "Restaurant marked as visited successfully!");
                        } else {
                            sendMessage(chatId, "Restaurant not found in your visit list.");
                        }
                    })
                    .doOnError(throwable -> {
                        log.error("Failed to mark restaurant as visited", throwable);
                        sendMessage(chatId, "Failed to mark restaurant as visited: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /markvisited [restaurant_id]");
        }
    }


    private void handleRemoveVisitCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String restaurantId = parts[1];
            Long userId = sessionService.getUserId(chatId);

            userService.getVisitList(userId)
                    .filter(visit -> visit.getRestaurantId().equals(restaurantId))
                    .collectList()
                    .flatMap(visits -> {
                        if (visits.isEmpty()) {
                            return Mono.just("Restaurant not found in your visit list.");
                        } else {
                            return userService.removeVisit(userId, restaurantId)
                                    .then(Mono.just("Restaurant removed from visit list successfully!"));
                        }
                    })
                    .doOnSuccess(response -> sendMessage(chatId, response))
                    .doOnError(throwable -> {
                        log.error("Failed to remove restaurant from visit list", throwable);
                        sendMessage(chatId, "Failed to remove restaurant from visit list: " + throwable.getMessage());
                    })
                    .subscribe();
        } else {
            sendMessage(chatId, "Invalid format. Use: /removevisit [restaurant_id]");
        }
    }



    /*private void sendMainMenuWithButton(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Choose how ....");

        // Создание Reply-клавиатуры
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строка клавиатуры
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("location"));
        row1.add(new KeyboardButton("search filters"));

        // Вторая строка клавиатуры
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Start searching"));

        // Добавляем строки в клавиатуру
        keyboard.add(row1);
        keyboard.add(row2);

        // Устанавливаем клавиатуру в сообщение
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true); // Для автоматического изменения размера
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Клавиатура не исчезает после нажатия
        message.setReplyMarkup(replyKeyboardMarkup);

        // Отправляем сообщение с клавиатурой
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }*/

    private void sendCustomMenu(long chatId, List<String> buttonNames, int buttonsPerRow) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Choose an option:");

        // Создание Reply-клавиатуры
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < buttonNames.size(); i++) {
            // Если это последняя кнопка, добавляем ее в новую строку
            if (i == buttonNames.size() - 1) {
                // Добавляем предыдущие кнопки, если они есть
                if (!row.isEmpty()) {
                    keyboard.add(row);
                }
                // Создаем отдельную строку для последней кнопки
                KeyboardRow lastRow = new KeyboardRow();
                lastRow.add(new KeyboardButton(buttonNames.get(i)));
                keyboard.add(lastRow);
            } else {
                row.add(new KeyboardButton(buttonNames.get(i)));

                // Если количество кнопок в строке достигает buttonsPerRow, создаем новую строку
                if ((i + 1) % buttonsPerRow == 0) {
                    keyboard.add(row);  // Добавляем текущую строку в клавиатуру
                    row = new KeyboardRow();  // Создаем новую строку
                }
            }
        }

        // Устанавливаем клавиатуру в сообщение
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true); // Для автоматического изменения размера
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Клавиатура исчезает после нажатия
        message.setReplyMarkup(replyKeyboardMarkup);

        // Отправляем сообщение с клавиатурой
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void handleFindRestaurantCommand(long chatId) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        List<String> buttons = Arrays.asList("Location", "Search Filters", "Start Searching");

        //sendMainMenuWithButton(chatId);
        sendCustomMenu(chatId, buttons, 2); // Создает меню, где последняя кнопка всегда в новой строке


        /*String[] parts = messageText.split(",", 4);
        if (parts.length < 2) {
            sendMessageWithMarkdown(chatId, "Invalid format. Use: /findrestaurant location, categoryID, keywords1 ... " +
                    "keywordsn, skipCategories1 ... skipCategoriesn\n\nWhere the location is a specific city, you can enter it in both Russian and English." +
                    "\n\nWhere the categoryID is specified in the format of the number" +
                    " 1300-13392.\n\nThe breakdown of the categories can be viewed on the [website](https://docs.foursquare.com/data-products/docs/categories).");
            return;
        }

        String location = parts[0].trim();
        String cuisine = parts.length >= 2 ? parts[1].trim() : "";
        String keywords = parts.length >= 3 ? parts[2].trim() : "";
        String skipCategories = parts.length == 4 ? parts[3].trim() : "";

        final String finalLocation = location;
        final String finalCuisine = cuisine;

        Long userId = sessionService.getUserId(chatId);
        Mono<List<String>> preferencesMono = userService.getUserPreferences(userId).collectList();
        Mono<List<String>> allergiesMono = userService.getUserAllergies(userId).collectList();

        Mono.zip(preferencesMono, allergiesMono)
                .flatMap(tuple -> {
                    List<String> preferences = tuple.getT1();
                    List<String> allergies = tuple.getT2();

                    final String finalKeywords = keywords.isEmpty() && !preferences.isEmpty() ? String.join(",", preferences) : keywords;
                    final String finalSkipCategories = skipCategories.isEmpty() && !allergies.isEmpty() ? String.join(",", allergies) : skipCategories;

                    return foursquareService.searchRestaurants(finalLocation, finalCuisine, finalKeywords, finalSkipCategories);
                })
                .doOnSuccess(response -> {
                    JsonNode results = response.get("results");
                    if (results.isArray() && results.size() > 0) {
                        for (JsonNode restaurant : results) {
                            String name = restaurant.get("name").asText();
                            String address = restaurant.get("location").get("formatted_address").asText();
                            String link = "https://foursquare.com/v/" + restaurant.get("fsq_id").asText();

                            // Создание сообщения с кнопкой
                            SendMessage message1 = new SendMessage();
                            message1.setChatId(String.valueOf(chatId));
                            message1.setText(name + ", " + address);

                            // Создание кнопки
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();

                            InlineKeyboardButton button = new InlineKeyboardButton();
                            button.setText("View Restaurant");
                            button.setUrl(link);  // Устанавливаем ссылку на ресторан

                            // Добавляем кнопку в строку
                            rowInline.add(button);

                            // Добавляем строку в список строк
                            rowsInline.add(rowInline);

                            // Устанавливаем разметку с кнопками в сообщение
                            inlineKeyboardMarkup.setKeyboard(rowsInline);
                            message1.setReplyMarkup(inlineKeyboardMarkup);

                            // Отправляем сообщение
                            sendAnswerMessage(message1);
                        }
                    } else {
                        sendMessage(chatId, "No restaurants found.");
                    }
                })
                .doOnError(throwable -> {
                    log.error("Failed to find restaurant", throwable);
                    sendMessage(chatId, "Failed to find restaurant: " + throwable.getMessage());
                })
                .subscribe();*/
    }


    private void locationButtonPushed(long chatId) {
        removeKeyboard(chatId);

        List<String> buttons = Arrays.asList("Distance", "Send my location", "Choose city", "Go to Searching Menu");
        sendCustomMenu(chatId, buttons, 2); // Создает меню, где последняя кнопка всегда в новой строке
    }

    private void sendMyLocation(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please share your location:");

        // Создание клавиатуры
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true); // Для автоматического изменения размера
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Клавиатура исчезает после нажатия

        // Создание строки с кнопкой
        KeyboardRow row = new KeyboardRow();
        KeyboardButton locationButton = new KeyboardButton("Share Location");
        locationButton.setRequestLocation(true);  // Эта кнопка будет запрашивать геолокацию
        row.add(locationButton);

        // Добавляем строку в клавиатуру
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);

        // Присоединяем клавиатуру к сообщению
        message.setReplyMarkup(replyKeyboardMarkup);

        // Отправляем сообщение с клавиатурой
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void chooseCity(long chatId) {
        sendMessage(chatId, "Write name of the City");
        this.choosingCityFlag = true;
    }

    private void chooseDistance(long chatId) {
        sendMessage(chatId, "Write the search radius in kilometers");
        this.choosingDistanceFlag = true;
    }




    private void handleOption2(long chatId) {
        sendMessage(chatId, "2!");
        // Добавить любое действие для Опции 2
        removeKeyboard(chatId);
    }

    private void handleOption3(long chatId) {
        sendMessage(chatId, "3!");
        // Добавить любое действие для Опции 3
        removeKeyboard(chatId);
    }

    private void removeKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        //message.setText("Клавиатура убрана.");

        // Создаем объект для удаления клавиатуры
        ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
        keyboardMarkup.setRemoveKeyboard(true); // Устанавливаем, что нужно удалить клавиатуру
        message.setReplyMarkup(keyboardMarkup);
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

            foursquareService.searchRestaurants(location, "", "", "") // Используем метод поиска ресторанов
                    .doOnSuccess(response -> {
                        StringBuilder responseMessage = new StringBuilder("Random restaurant:\n");
                        JsonNode results = response.get("results");
                        if (results.isArray() && results.size() > 0) {
                            List<JsonNode> restaurantList = new ArrayList<>();
                            results.forEach(restaurantList::add);
                            Collections.shuffle(restaurantList); // Перемешиваем список результатов

                            JsonNode restaurant = restaurantList.get(0); // Берем первый ресторан из перемешанного списка
                            String name = restaurant.get("name").asText();
                            String address = restaurant.get("location").get("formatted_address").asText();
                            String fsqId = restaurant.get("fsq_id").asText();
                            String link = "https://foursquare.com/v/" + fsqId; // Конструируем ссылку с помощью fsq_id

                            responseMessage.append(name).append(", ").append(address).append("\n").append("Link: ").append(link).append("\n");
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
            // Разделяем аллергии по запятой
            String[] allergies = parts[1].split(",");

            Long userId = sessionService.getUserId(chatId);

            for (String rawAllergy : allergies) {
                String allergy = rawAllergy.trim(); // Убираем пробелы

                // Проверка регулярным выражением: только буквы русского и английского алфавита
                if (!allergy.matches("[a-zA-Zа-яА-Я]+")) {
                    sendMessage(chatId, "Invalid allergy format for: " + allergy + ". Only alphabetic characters are allowed.");
                    continue;
                }

                // Создаем финальную переменную для использования в лямбде
                final String finalAllergy = allergy;

                // Добавляем аллергию
                userService.addUserAllergy(userId, finalAllergy)
                        .doOnSuccess(aVoid -> sendMessage(chatId, "Allergy '" + finalAllergy + "' added successfully!"))
                        .doOnError(throwable -> {
                            log.error("Failed to add allergy: " + finalAllergy, throwable);
                            sendMessage(chatId, "Failed to add allergy '" + finalAllergy + "': " + throwable.getMessage());
                        })
                        .subscribe();
            }
        } else {
            sendMessage(chatId, "Invalid format. Use: /addallergy allergy1, allergy2, ...");
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
            // Разделяем предпочтения по запятой
            String[] preferences = parts[1].split(",");

            Long userId = sessionService.getUserId(chatId);

            for (String rawPreference : preferences) {
                String preference = rawPreference.trim(); // Убираем пробелы

                // Проверка регулярным выражением: только буквы русского и английского алфавита
                if (!preference.matches("[a-zA-Zа-яА-Я]+")) {
                    sendMessage(chatId, "Invalid preference format for: " + preference + ". Only alphabetic characters are allowed.");
                    continue;
                }

                // Создаем финальную переменную для использования в лямбде
                final String finalPreference = preference;

                // Добавляем предпочтение
                userService.addUserPreference(userId, finalPreference)
                        .doOnSuccess(aVoid -> sendMessage(chatId, "Preference '" + finalPreference + "' added successfully!"))
                        .doOnError(throwable -> {
                            log.error("Failed to add preference: " + finalPreference, throwable);
                            sendMessage(chatId, "Failed to add preference '" + finalPreference + "': " + throwable.getMessage());
                        })
                        .subscribe();
            }
        } else {
            sendMessage(chatId, "Invalid format. Use: /addpref preference1, preference2, ...");
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

        // Create keyboard markup
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Row 1
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/register"));
        row1.add(new KeyboardButton("/login"));
        row1.add(new KeyboardButton("/logout"));
        keyboard.add(row1);

        // Row 2
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/help"));
        keyboard.add(row2);

        // Set the keyboard
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