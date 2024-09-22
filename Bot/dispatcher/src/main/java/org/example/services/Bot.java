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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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
import java.util.*;

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
    private Boolean enterPrefencesFlag;
    private Boolean enterAllergyFlag;
    private Boolean enterSortByFlag;
    private Boolean enterPricePolicyFlag;
    private Boolean enterOpenNowFlag;
    private Boolean addAllergyFlag;
    private Boolean delAllergyFlag;
    private Boolean addPrefFlag;
    private Boolean delPrefFlag;
    private Boolean addVisitFlag;
    private Boolean markVisitedFlag;
    private Boolean removevisitFlag;
    // Для хранения параметров поиска пользователей
    private final Map<Long, SearchParameters> userSearchParameters = new HashMap<>();



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

        this.enterPrefencesFlag = false;
        this.enterAllergyFlag = false;
        this.enterSortByFlag = false;
        this.enterPricePolicyFlag = false;
        this.enterOpenNowFlag = false;

        this.addAllergyFlag = false;
        this.delAllergyFlag = false;
        this.addPrefFlag = false;
        this.delPrefFlag = false;

        this.addVisitFlag = false;
        this.markVisitedFlag = false;
        this.removevisitFlag = false;
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

                    // Получаем или создаем новый объект SearchParameters для пользователя
                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    // Сохраняем координаты для буферизации
                    params.setLatitude(latitude);
                    params.setLongitude(longitude);

                    // Отправляем сообщение с информацией о геолокации
                    sendMessage(chatId, "Your location: " + latitude + ", " + longitude);

                    locationButtonPushed(chatId);
                }
                else if (choosingCityFlag) {
                    sendMessage(chatId, "You Enter - " + messageText);
                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    // Сохраняем город для буферизации
                    params.setNear(messageText);
                    this.choosingCityFlag = false;
                }

                else if (choosingDistanceFlag) {
                    Double distance = Double.parseDouble(messageText);
                    sendMessage(chatId, "You Enter - " + distance +"m");

                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    params.setArea(distance);
                    this.choosingDistanceFlag = false;
                }

                else if (enterPrefencesFlag) {
                    sendMessage(chatId, "You Enter - " + messageText);

                    this.enterPrefencesFlag = false;
                    String cleanedInput = messageText.replaceAll("[^a-zA-Z,]", "");


                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    // Добавляем новые предпочтения к полю query
                    String currentQuery = params.getQuery();
                    if (currentQuery == null || currentQuery.isEmpty()) {
                        params.setQuery(cleanedInput); // Если query еще пусто, то просто задаем новое значение
                    } else {
                        params.setQuery(currentQuery + "," + cleanedInput); // Если есть значение, добавляем новое через запятую
                    }

                }

                else if (enterAllergyFlag) {
                    sendMessage(chatId, "You Enter - " + messageText);

                    this.enterAllergyFlag = false;
                    String cleanedInput = messageText.replaceAll("[^a-zA-Z,]", "");

                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    // Добавляем новые аллергии к полю allergy
                    String currentAllergy = params.getAllergy();
                    if (currentAllergy == null || currentAllergy.isEmpty()) {
                        params.setAllergy(cleanedInput); // Если allergy еще пусто, то просто задаем новое значение
                    } else {
                        params.setAllergy(currentAllergy + "," + cleanedInput); // Если есть значение, добавляем новое через запятую
                    }
                }


                else if (messageText.equals("/start")) {
                    sendMainMenu(chatId);
                } else if (messageText.equals("/help")) {
                    sendHelpMessage(chatId);
                } else if (messageText.startsWith("/register")) {
                    handleRegisterCommand(chatId, messageText);
                } else if (messageText.startsWith("/login")) {
                    int messageId = update.getMessage().getMessageId();
                    handleLoginCommand(chatId, messageText, messageId);
                } else if (messageText.startsWith("/logout")) {
                    handleLogoutCommand(chatId);
                }  else if (messageText.startsWith("/viewprefs")) {
                    handleViewPreferencesCommand(chatId);}


                else if (messageText.startsWith("/addpref")) {
                    sendMessage(chatId, "If there are many preferences to add, you must enter them separated by commas." +
                            "\nExample: coffee,tea,...");
                    this.addPrefFlag = true;}

                else if (addPrefFlag) {
                    this.addPrefFlag = false;
                    handleAddPreferenceCommand(chatId, messageText);
                }

                else if (messageText.startsWith("/delpref")) {
                    sendMessage(chatId, "If there are many preferences to delete, you must enter them separated by commas." +
                            "\nExample: coffee,tea,...");
                    this.delPrefFlag = true;}

                else if (delPrefFlag) {
                    this.delPrefFlag = false;
                    handleDeletePreferenceCommand(chatId, messageText);
                }


                else if (messageText.startsWith("/addallergy")) {
                    sendMessage(chatId, "If there are many allergies to add, you must enter them separated by commas." +
                            "\nExample: tomatoes,strawberry,...");
                    this.addAllergyFlag = true;}

                else if (addAllergyFlag) {
                    this.addAllergyFlag = false;
                    handleAddAllergyCommand(chatId, messageText);
                }

                else if (messageText.startsWith("/delallergy")) {
                    sendMessage(chatId, "If there are many allergies to delete, you must enter them separated by commas." +
                            "\nExample: tomatoes,strawberry,...");
                    this.delAllergyFlag = true;}

                else if (delAllergyFlag) {
                    this.delAllergyFlag = false;
                    handleDeleteAllergyCommand(chatId, messageText);
                }

                else if (messageText.startsWith("/viewallergies")) {
                    handleViewAllergiesCommand(chatId);

                } else if (messageText.startsWith("/findrestaurant")) {
                    handleFindRestaurantCommand(chatId);

                } else if (messageText.startsWith("Location")) {
                    locationButtonPushed(chatId);
                } else if (messageText.startsWith("Search Filters")) {
                    searchFiltersButtonPushed(chatId);
                } else if (messageText.startsWith("Start Searching")) {
                    startSearching(chatId);

                } else if (messageText.startsWith("Send my location")) {
                    sendMyLocation(chatId);
                } else if (messageText.startsWith("Choose city")) {
                    chooseCity(chatId);
                } else if (messageText.startsWith("Distance")) {
                    chooseDistance(chatId);
                } else if (messageText.startsWith("Go to Searching Menu")) {
                    removeKeyboard(chatId);
                    handleFindRestaurantCommand(chatId);}


                else if (messageText.startsWith("Preferences")) {
                    preferences(chatId);}
                else if (messageText.startsWith("Allergy")) {
                    allergy(chatId);}


                else if (messageText.startsWith("Sort by")) {
                    sortBy(chatId);}

                else if (messageText.matches("^/(relevance|rating|distance|popularity)$") && enterSortByFlag) {

                    this.enterSortByFlag = false;
                    String cleanedInput = messageText.replaceAll("[/]", "").toUpperCase();

                    sendMessage(chatId, "You Enter - " + cleanedInput);

                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    params.setSort(cleanedInput);

                }

                else if (messageText.startsWith("Pricing policy")) {
                    pricingPolicy(chatId);}

                else if (messageText.matches("^/[1-4]$") && enterPricePolicyFlag) {
                    this.enterPricePolicyFlag = false;
                    String cleanedInput = messageText.replaceAll("[/]", "");

                    sendMessage(chatId, "You Enter - " + cleanedInput);

                    Integer maxPrice = Integer.parseInt(cleanedInput);

                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    params.setMaxPrice(maxPrice);
                }

                //"Open Now"
                else if (messageText.startsWith("Open Now")) {
                    openNow(chatId);}

                else if (messageText.matches("^/(yes|no)$") && enterOpenNowFlag) {
                    this.enterOpenNowFlag = false;

                    String cleanedInput = messageText.replaceAll("[/]", "");
                    Boolean result = cleanedInput.equals("yes");

                    sendMessage(chatId, "You Enter - " + result);

                    SearchParameters params = userSearchParameters.computeIfAbsent(chatId, k -> new SearchParameters());

                    params.setOpenNow(result);
                }


                else if (messageText.startsWith("/randomrestaurant")) {
                    handleRandomRestaurantCommand(chatId, messageText);


                } else if (messageText.startsWith("/visitlist")) {
                    handleVisitListCommand(chatId);
                }
                else if (messageText.startsWith("/showlist")) {
                    handleShowListCommand(chatId);
                }

                else if (messageText.startsWith("/addvisit")) {
                    sendMessage(chatId, "You need to enter places one at a time!");
                    this.addVisitFlag = true;}

                else if (addVisitFlag) {
                    this.addVisitFlag = false;
                    handleAddVisitCommand(chatId, messageText);
                }

                else if (messageText.startsWith("/markvisited")) {
                    sendMessage(chatId, "You need to enter places one at a time!");
                    this.markVisitedFlag = true;}

                else if (markVisitedFlag) {
                    this.markVisitedFlag = false;
                    handleMarkVisitedCommand(chatId, messageText);
                }

                else if (messageText.startsWith("/removevisit")) {
                    sendMessage(chatId, "You need to enter places one at a time!");
                    this.removevisitFlag = true;}

                else if (removevisitFlag) {
                    this.removevisitFlag = false;
                    handleRemoveVisitCommand(chatId, messageText);
                }

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
                "/addpref - Add a new preference\n" +
                "/viewprefs - View your preferences\n" +
                "/delpref - Delete a preference\n" +
                "/addallergy - Add a new allergy\n" +
                "/viewallergies - View your allergies\n" +
                "/delallergy - Delete an allergy\n" +
                "/findrestaurant - Find a restaurant\n" +
                "/randomrestaurant [location] [radius] - Find a random restaurant\n" +
                "/visitlist - Show visit-related commands\n" +
                "/addvisit - Add a restaurant to your visit list\n" +
                "/showlist - Show your visit list\n" +
                "/markvisited - Mark a restaurant as visited\n" +
                "/removevisit - Remove a restaurant from your visit list";

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

        String restaurantId = messageText;
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

        String restaurantId = messageText;
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
    }

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

        sendCustomMenu(chatId, buttons, 2); // Создает меню, где последняя кнопка всегда в новой строке
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
        sendMessage(chatId, "Write the search radius in meters");
        this.choosingDistanceFlag = true;
    }


    private void searchFiltersButtonPushed(long chatId) {
        removeKeyboard(chatId);

        List<String> buttons = Arrays.asList("Preferences", "Allergy", "Sort by","Pricing policy",
                "Open Now", "Go to Searching Menu");
        sendCustomMenu(chatId, buttons, 3);
    }



    private void preferences(long chatId) {
        sendMessage(chatId, "If there are many preferences, you must enter them separated by commas." +
                "\nExample: coffee, tea, ...");
        this.enterPrefencesFlag = true;
    }
    private void allergy(long chatId) {
        sendMessage(chatId, "If there are many allergies, you must enter them separated by commas." +
                "\nExample: tomatoes, strawberry, ...");
        this.enterAllergyFlag = true;
    }

    private void sortBy(long chatId) {
        sendMessage(chatId, "Select the option to sort:" +
                "\n  /relevance\n  /rating\n  /distance\n  /popularity");
        this.enterSortByFlag = true;
    }

    private void pricingPolicy(long chatId) {
        sendMessage(chatId, "Select the option from Prising List:" +
                "\n  /1 - \uD83D\uDCB0 \n  /2 - \uD83D\uDCB0\uD83D\uDCB0 \n  " +
                "/3 - \uD83D\uDCB0\uD83D\uDCB0\uD83D\uDCB0 \n  /4 - \uD83D\uDCB0\uD83D\uDCB0\uD83D\uDCB0\uD83D\uDCB0");
        this.enterPricePolicyFlag = true;
    }

    private void openNow(long chatId) {
        sendMessage(chatId, "Is it important for you that the restaurant is open right now" +
                "\n  /yes \n  /no");
        this.enterOpenNowFlag = true;
    }


    private void startSearching(long chatId) {
        sendMessage(chatId, "Results:");

        // Получаем параметры поиска пользователя из буфера
        SearchParameters params = userSearchParameters.get(chatId);

        Long userId = sessionService.getUserId(chatId);

        Mono<List<String>> preferencesMono = userService.getUserPreferences(userId).collectList();

        preferencesMono
                .flatMap(preferences -> {
                    String keywords = params.getQuery();
                    if (keywords.isEmpty() && !preferences.isEmpty()) {
                        keywords = String.join(",", preferences); // Вставляем предпочтения пользователя
                    }

                    String location = params.getNear();
                    String sort = params.getSort();
                    Boolean openNow = params.getOpenNow();
                    Integer maxPrice = params.getMaxPrice();
                    Double latitude = params.getLatitude();
                    Double longitude = params.getLongitude();

                    return foursquareService.searchRestaurants(location, keywords, sort, openNow, maxPrice, latitude, longitude);
                })
                .doOnSuccess(response -> {
                    JsonNode results = response.get("results");
                    if (results.isArray() && results.size() > 0) {
                        for (JsonNode restaurant : results) {
                            String name = restaurant.get("name").asText();
                            String address = restaurant.get("location").get("formatted_address").asText();
                            String link = "https://foursquare.com/v/" + restaurant.get("fsq_id").asText();

                            // Отправка данных ресторана пользователю
                            sendMessage(chatId, name + " - " + address + "\n" + link);
                        }
                    } else {
                        sendMessage(chatId, "No restaurants found.");
                    }
                })
                .doOnError(throwable -> {
                    log.error("Failed to find restaurants", throwable);
                    if (throwable.getMessage().contains("400")) {
                        sendMessage(chatId, "No restaurants found. Please check your query or try again with different parameters.");
                    } else {
                        sendMessage(chatId, "Failed to find restaurants.");
                    }
                })
                .subscribe();

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
        /*if (!sessionService.isUserLoggedIn(chatId)) {
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
        }*/
    }


    private void handleAddAllergyCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        messageText = messageText.replaceAll("[^a-zA-Zа-яА-Я, ]", "");
        String[] allergies = messageText.split(",");

        Long userId = sessionService.getUserId(chatId);

        for (String allergy : allergies) {

            String tmp = allergy;
            while (tmp.charAt(0)==' ' && (!tmp.isEmpty())){
                tmp = tmp.substring(1);
            }
            // Создаем финальную переменную для использования в лямбде
            final String finalAllergy = tmp;


            // Добавляем аллергию
            userService.addUserAllergy(userId, finalAllergy)
                    .doOnSuccess(aVoid -> sendMessage(chatId, "Allergy '" + finalAllergy + "' added successfully!"))
                    .doOnError(throwable -> {
                        log.error("Failed to add allergy: " + finalAllergy, throwable);
                        sendMessage(chatId, "Failed to add allergy '" + finalAllergy + "': " + throwable.getMessage());
                    })
                    .subscribe();
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

        messageText = messageText.replaceAll("[^a-zA-Zа-яА-Я, ]", "");
        String[] allergiesArray = messageText.split(",");

        Long userId = sessionService.getUserId(chatId);

        for (String allergy : allergiesArray) {

            String tmp = allergy;
            while (tmp.charAt(0)==' ' && (!tmp.isEmpty())){
                tmp = tmp.substring(1);
            }
            // Создаем финальную переменную для использования в лямбде
            final String finalAllergy = tmp;

            // Удаляем аллергию
            userService.deleteUserAllergy(userId, finalAllergy)
                    .doOnSuccess(aVoid -> sendMessage(chatId, "Allergy '" + finalAllergy + "' deleted successfully!"))
                    .doOnError(throwable -> {
                        log.error("Failed to delete allergy: " + finalAllergy, throwable);
                        sendMessage(chatId, "Failed to delete allergy '" + finalAllergy + "': " + throwable.getMessage());
                    })
                    .subscribe();
        }
    }

    private void handleAddPreferenceCommand(long chatId, String messageText) {
        if (!sessionService.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "You are not logged in.");
            return;
        }

        messageText = messageText.replaceAll("[^a-zA-Zа-яА-Я, ]", "");
        String[] preferences = messageText.split(",");

        Long userId = sessionService.getUserId(chatId);

        for (String preference : preferences) {

            String tmp = preference;
            while (tmp.charAt(0)==' ' && (!tmp.isEmpty())){
                tmp = tmp.substring(1);
            }
            // Создаем финальную переменную для использования в лямбде
            final String finalPreference = tmp;


            // Добавляем предпочтение
            userService.addUserPreference(userId, finalPreference)
                    .doOnSuccess(aVoid -> sendMessage(chatId, "Preference '" + finalPreference + "' added successfully!"))
                    .doOnError(throwable -> {
                        log.error("Failed to add preference: " + finalPreference, throwable);
                        sendMessage(chatId, "Failed to add preference '" + finalPreference + "': " + throwable.getMessage());
                    })
                    .subscribe();
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

        messageText = messageText.replaceAll("[^a-zA-Zа-яА-Я, ]", "");
        String[] preferenceArray = messageText.split(",");

        Long userId = sessionService.getUserId(chatId);

        userService.getUserPreferences(userId)
                .collectList()
                .flatMap(preferences -> {
                    StringBuilder responseMessage = new StringBuilder();

                    // Проходим по каждому предпочтению
                    for (String preference : preferenceArray) {
                        preference = preference.trim(); // Убираем пробелы

                        if (preferences.contains(preference)) {
                            // Удаляем предпочтение
                            userService.deleteUserPreference(userId, preference)
                                    .subscribe(); // Выполняем удаление

                            responseMessage.append(preference).append(" deleted successfully!\n");
                        } else {
                            responseMessage.append(preference).append(" does not exist in your list.\n");
                        }
                    }
                    return Mono.just(responseMessage.toString()); // Возвращаем итоговое сообщение
                })
                .doOnNext(response -> sendMessage(chatId, response))
                .doOnError(throwable -> {
                    log.error("Failed to delete preference", throwable);
                    sendMessage(chatId, "Failed to delete preference: " + throwable.getMessage());
                })
                .subscribe();
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

    public void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);

        // Выполнение удаления сообщения
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to delete message", e);
        }
    }

    private void handleLoginCommand(long chatId, String messageText, int messageId) {
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
                            deleteMessage(chatId, messageId);

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