# Telegram_Restaurant_Bot






# Realisation

## Dispatcher/cofiguration

### `BotConfiguration.java`
This file is a configuration class for Spring, which:
- Uses the `@Configuration` annotation, indicating that this is a Java configuration.
- Loads properties from the `app.properties' file using the `@PropertySource` annotation.
- Defines fields for the bot name (`name`), token (`token`) and URL (`url`), which are taken from the properties file via the `@Value` annotation.
- Contains getters for these fields, which allows other components of the application to access the bot configuration.

**Task:** Managing bot settings through the properties file and providing access to these values to other parts of the application.

---

###  `BotInitialization.java`
This file is responsible for initializing and registering the bot in the Telegram API:
- The class is marked with the annotation `@Component`, which makes it a Spring component.
- In the constructor, using the annotation `@Autowired`, a dependency is introduced on the `BotConfiguration` and `AnnotationConfigApplicationContext`, which contains the bot bean.
- The `init()` method is started automatically at the `ContextRefreshedEvent` event due to the `@EventListener` annotation. An instance of `TelegramBotsApi` is created in it, and the bot is registered via `telegramBotsApi.registerBot(bot)'.

**Task:** Initialization and registration of the bot in Telegram when launching the application.

---

### `DispatcherConfiguration.java`
This file is a configuration class for Spring with the `dispatcher` profile:
- Marked with the annotation `@Profile("dispatcher")`, which means that this configuration will be activated only if the `dispatcher` profile is enabled.
- The annotation `@ComponentScan` indicates to Spring the need to scan packages to find components and beans.

**Task:** Defining the configuration for the `dispatcher` profile, allowing you to activate certain components only for this profile.

---

###  `TomcatConfig.java`
This file configures the built-in Tomcat server:
- The class is marked with the annotation `@Configuration`, which indicates that it is a configuration class.
- The `ServerPort` field is initialized with the value from the properties file with the default value `8084'.
- The `tomcat()` method is marked with the `@Bean` annotation, creating and configuring an instance of the Tomcat server. The server starts with the specified port, initializes and returns.

**Task:** Configuring and launching the embedded Tomcat server with the specified port for the web application.

---

These files jointly configure the Telegram bot via Spring, manage configuration profiles, and run the Tomcat server to service the application.
---
## Dispatcher/controller

This class is an `UpdateController.java` manages the processing of incoming updates (messages) from Telegram and distributes them by type. 

- **Class:** `UpdateController`
  - **Annotation `@Component`:** Indicates that this class is a Spring component and can be auto-scanned for dependency injection.
  - **Annotation `@Log4j2`:** Automatically adds a logger for logging events.

### Fields:
- **`Bot bot`:** This field stores an instance of the bot through which responses are sent to the user.
- **`MessageUtils messageUtils`:** A field for message processing utilities, used to create messages and responses.
- **`UpdateProducer updateProducer`:** The field is responsible for sending updates to the queue for further processing.

### Constructor:
- **`UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer)`**
- Implements dependencies through the constructor to work with message utilities and the update producer (probably with RabbitMQ).

### Methods:

 **`registerBot(Bot bot)`**
   - Registers the bot so that the controller can send messages through it.

**`ProcessUpdate(Update update)`**
is the main method for processing incoming updates. Checks if the update is `null' and then passes it on for further processing.
   - If the message is not supported, logs an error.

 **`distributeMessagesByType(Update update)`**
- Distributes messages by type (text, documents, photos) and transmits them for appropriate processing.
   - If the message type is not supported, sends a notification to the user about the unsupported type.

**`setUnsupportedMessageTypeView(Update update)`**
   - Sends a message to the user about an unsupported message type.

 **`setFileIsReceivedView(Update update)`**
- Sends a message to the user about an invalid file with a suggestion to use the command `\\help`.

 **`setView(SendMessage sendMessage)`**
- Sends a response to the user using the bot using the 'SendMessage` object.

**Processing message types:**
- **`processPhotoMessage(Update update)`** — Sends the photo for processing and notifies the user when the file is received.
   - **`processDocMessage(Update update)`** — Sends the document for processing and notifies the user when the file is received.
   - **`processTextMessage(Update update)`** — Sends a text message for processing.

### The main task of the class:
The UpdateController class is responsible for receiving updates from Telegram, distributing them by type (text, document, photo), sending appropriate notifications to users and transferring updates for further processing to the queue.
---

---
## RabbitMQ
The RabbitMQ message broker has been added to the project, however, queues are formed on this version, but are not used. It will be updated in the future.
---

## Dispatcher/service

###  `AnswerConsumerImpl.java`

**Task:** This class is an implementation of a message consumer that processes responses sent from another system through a message broker (RabbitMQ). He receives messages from the queue and sends them to users via a Telegram bot.

- **Annotation `@Service`:** The class is marked as a service, which makes it a Spring component capable of being automatically embedded in other parts of the application.
- **Annotation `@Log4j2`:** Adds the ability to keep logs.
- **The `UpdateController updateController` field:** This is the controller through which responses are sent to users.
- **Designer:** When creating an object, a dependency on the `UpdateController` is implemented and a log message about the creation of a consumer is recorded.
- **The `consume(SendMessage SendMessage) method`:**
- Marked with the annotation `@RabbitListener`, which indicates that this method listens to the specified message broker (queue `ANSWER_MESSAGE').
  - Gets the `SendMessage` object from RabbitMQ and calls the `setView()' method` the controller to send a message to the user.
  - Logs the event of receiving a message.

### Main task: Processing responses from the RabbitMQ queue and sending them to users via Telegram.

---

###  `UpdateProducerImpl.java`

**Task:** This class is an implementation of the message producer, which sends updates (messages) to the RabbitMQ queue for further processing in other parts of the system.

- **Annotation `@Service`:** The class is marked as a service, which allows Spring to automatically manage its lifecycle.
- **Annotation `@Log4j2`:** Adds the ability to keep logs.
- **The `RabbitTemplate RabbitTemplate` field:** A template for sending messages to RabbitMQ.
- **Designer:** The RabbitMQ template is implemented using the `@Autowired` annotation and a specific instance using `@Qualifier("exampleRabbitTemplate")'.
- **The `produce(String rabbitQueue, Update update)` method:**
- Logs the message text.
  - Sends the `Update` object to the specified queue (`rabbitQueue') using the `convertAndSend()` method.

### Main task: Sending messages received by the bot to the RabbitMQ queue for further processing by other system components.

---

### Class Interaction:
- **`AnswerConsumerImpl`** consumes messages from the `ANSWER_MESSAGE` queue and sends them to users via Telegram.
- **`UpdateProducerImpl`** sends updates (text messages, documents, photos) to the RabbitMQ queue for processing.

Both classes work with a message broker (RabbitMQ), organizing data exchange between different parts of the application.

---
## Dispatcher/services


The `SearchParameters` class responsible for storing and managing the search parameters for the application. This class provides convenient getters, setters, and a method for generating a string of GET request parameters. 


- **Fields:**
- `query`: search query, string.
  - `allergy`: allergy, used to filter the results.
  - `near`: the nearest city or location (for example, St. Petersburg).
  - `openNow`: A filter for searching only open places (Boolean value).
  - `sort': A sorting parameter (for example, by distance or relevance).
  - `minPrice': minimum price, integer.
  - `area': the search area, a numeric value of type `Double'.
  - `maxPrice': the maximum price, an integer.
  - `limit': limit on the number of results (from 0 to 100,000).
  - `categories': search categories, an integer for filtering.
  - `latitude`: latitude for geo-search.
  - `longitude`: The longitude for the geo-search.

### Constructor:
The parameterless constructor initializes an object with predefined values:
- `query` and `allergy` are empty strings.
- `near` is an empty string.
- `openNow` is `false' by default.
- `sort` is set to "RELEVANCE".
- `minPrice` = 1, `maxPrice` = 4, `area` = 500.0, `limit` = 5.

### Getters and setters:
The `get` and `set` methods are implemented for each field, which allows you to get and change the values of search parameters.

### The `toQueryString()` method:
This method converts all parameters to a string in the format of a GET request. Example of a string:
```
query=some_query&near=City&open_now=true&sort=RELEVANCE&min_price=1&area=500.0&max_price=4&limit=5&categories=3&ll=45.0,90.0
```
The method makes it easy to generate a URL request with parameters for use in search queries over HTTP.

### The main task:
The `SearchParameters` class helps you manage search parameters and provides the ability to quickly convert them to a string for use in GET requests to the API or other services.



---
## Bot.java
This file contains an implementation of a Telegram bot that interacts with users, accepts commands and performs actions based on these commands.

 **onUpdateReceived(Update update)**  
   The main method of processing incoming messages and updates from Telegram. Determines which type of message was received (text, location, etc.), and causes appropriate processing depending on the content. Controls the logic of executing commands such as `/start`, `/help`, `/addpref`, `/delallergy`, as well as actions for sending and processing the user's location.

  **sendCustomMenu(long chatId, List<String> buttonNames, int buttonsPerRow)**
Creates a custom menu with buttons that is sent as a message to the user. The buttons are organized into rows according to the specified number of buttons per row.


---
### List of places

**handleAddVisitCommand(long chatId, String messageText)**  
   Adds a restaurant to the user's list of visits. Checks if the user is logged in and adds a new restaurant to his list of visits if there is no such restaurant there yet.

**handleShowListCommand(long chatId)**  
   Displays a list of restaurants added to the user's "list of visits". If a restaurant has been marked as visited, its link is displayed using HTML markup.

 **handleMarkVisitedCommand(long chatId, String messageText)**  
   Marks the restaurant as visited in the user's list of visits. If the restaurant is not found, a corresponding message is displayed.

**handleRemoveVisitCommand(long chatId, String messageText)**  
   Removes a restaurant from the user's list of visits. If the restaurant is not found, a corresponding message is displayed.   
---
### Preferences

   **handleAddAllergyCommand(long chatId, String messageText)**  
    Adds a user's allergy to their profile. Checks the validity of the allergy and updates the list of allergies for the current user.

 **handleViewAllergiesCommand(long chatId)**  
    Displays a list of the user's allergies. If there are no allergies, a corresponding message is displayed.

 **handleDeleteAllergyCommand(long chatId, String messageText)**  
    Removes the specified allergies from the user's profile.   
---
### Allergies

 **handleAddPreferenceCommand(long chatId, String messageText)**  
    Adds the user's preferences to their profile. The entered preferences are saved for further use in filtering restaurants.

 **handleViewPreferencesCommand(long chatId)**  
    Displays a list of the user's preferences. If there are no preferences, a corresponding message is displayed.

 **handleDeletePreferenceCommand(long chatId, String messageText)**  
    Deletes the specified preferences from the user's profile.   

---
### User authorization and data concealment

 **handleLoginCommand(long chatId, String messageText, int messageId)**  
    Processes the login command. If authentication is successful, the user logs in and the message with credentials is deleted.

 **deleteMessage(long chatId, int messageId)**  
Deletes a message that contains login and password by its ID in the chat.
---
### Search for a restaurant

There are two possible search modes. 

The random restaurant search searches for a random restaurant by a given city and search radius.

**handleRandomRestaurantCommand(long catid, String message Text)**  
    Performs a random search for restaurants by a given location and radius. Uses the user's preferences and allergies to filter the results.

There is also a standard search with parameters. 

 **start Searching(long cat Id)**  
    Searches for restaurants based on the user's entered parameters, such as location, preferences, allergies, pricing policy, whether the restaurant is open now, search radius, etc. The results are displayed as links to restaurants. You can also specify an option in the parameters to sort the results.

---

## DispatcherApplication.java

DispatcherApplication class.java is the entry point for launching an application that uses Spring to manage the configuration and initialization of the Telegram bot. This class controls the launch of the application context with activated profiles, configuration settings, and bot initialization.

---
