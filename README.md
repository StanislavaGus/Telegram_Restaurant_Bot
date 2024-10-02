# Telegram_Restaurant_Bot

## Task
### Features

1. **Sign up**
2. **Sign in**
3. **Preferences**
   - Show user preferences
   - Add preferences (e.g. vegetarian, allergies, Italian, fish, etc)
   - Delete preferences

4. **Find a restaurant**
   - Request a restaurant with query (location, kitchen, keywords, etc)
     - If some criteria are not set, user preferences should be used
     - Allow setting `skip` for criteria to skip it
   - Request a random restaurant (location + area)

5. **Visit list**
   - Add restaurant to visit list
   - Show visit list
   - Mark restaurant as visited
   - Remove restaurant from list

---
# Run the application

The application can be run by terminal command ***docker-compose up --build***, while containers with Rabbitmq images and databases will be started automatically in the docker.

Docker image of application - https://hub.docker.com/repository/docker/marg0sav/restaurantbot/general


Also, if you pre-launch containers, the application can be launched through the Dispatcher Application class.

---

# Using example

<img src="https://github.com/user-attachments/assets/d7345348-2dde-4daa-a56c-347b0e669434" width="300" />

### Registration

After a successful registration, the message with the password will be deleted from the chat.

<img src="https://github.com/user-attachments/assets/8b70d2fe-2b73-44e5-bed5-f663a10fa65f" width="300" />

<img src="https://github.com/user-attachments/assets/e6243172-8e8c-4058-bbad-d3831a5b8dde" width="300" />

<img src="https://github.com/user-attachments/assets/08225ab2-4f7a-4a98-83d8-39d095cc720c" width="370" />



### Login

After a successful login, the message with the password will be deleted from the chat.

<img src="https://github.com/user-attachments/assets/5f7f557e-2a8f-4625-9d4a-19f35390e3ce" width="300" />

<img src="https://github.com/user-attachments/assets/8ef82731-2532-474b-af3b-31cad09e2e78" width="300" />

### WishList
<img src="https://github.com/user-attachments/assets/eebf5ab4-7aa1-45a6-b5d9-db59cfa85173" width="300" />

<img src="https://github.com/user-attachments/assets/21224ae5-0737-4b18-abd6-d4683788d16a" width="300" />

### Preference
<img src="https://github.com/user-attachments/assets/2537b49f-cb10-44ec-9c67-605b40c86813" width="300" />

### Allergies
<img src="https://github.com/user-attachments/assets/a6aa724c-3ebc-4b56-ab35-9e79017c1c6b" width="300" />

### Find random restaurant
<img src="https://github.com/user-attachments/assets/6605bf5d-f534-4682-828e-0cd7339c4489" width="300" />

### Find restaurant (search with parameters)
<img src="https://github.com/user-attachments/assets/993537a9-67d5-4e80-8dfc-a5a4de4a86ed" width="300" />

#### Location
<img src="https://github.com/user-attachments/assets/3f0e6c22-ea36-45e0-9b8e-9dfaf1f9496b" width="300" />

<img src="https://github.com/user-attachments/assets/276f6b1f-ff40-4481-aab9-9351b6f7a098" width="300" />

#### Search Filters
<img src="https://github.com/user-attachments/assets/10ea0557-8a7c-4ee0-9588-3d2b0461434b" width="300" />

<img src="https://github.com/user-attachments/assets/574ad4ea-a0e6-40e7-8abf-c467ef83518a" width="300" />

#### Results
<img src="https://github.com/user-attachments/assets/bd813462-e5fc-43fe-90ce-e2c1e5370424" width="300" />

<img src="https://github.com/user-attachments/assets/a8aed645-c6df-4579-bfe1-7057b2c92b91" width="300" />


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
This file is a configuration class for Spring:
- The annotation `@ComponentScan` indicates to Spring the need to scan packages to find components and beans.
- The annotation `@PropertySource` declares an external properties file (app.properties) to be loaded into the Spring environment.

**Task:** This class sets up the configuration for the Spring application by defining where Spring should scan for components and which properties file should be used for configuration values. It is used when registering a bot.

---

###  `TomcatConfig.java`
This file configures the built-in Tomcat server:
- The class is marked with the annotation `@Configuration`, which indicates that it is a configuration class.
- The `ServerPort` field is initialized with the value from the properties file with the default value `8088'.
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

## Node/cofiguration

### `FoursquareConfig.java`

This class configures the HTTP client to work with the Foursquare API and provides an API key for interacting with the service.

- **Annotation `@Configuration`:** Defines the class as a configuration class.
- **The `OkHttpClient()` method:** Returns the 'OkHttpClient` bin for executing HTTP requests.
- **The `foursquareApiKey() method`:** Returns the API key for accessing Foursquare.

**Task:** Configuring the HTTP client to work with the Foursquare API and providing an API key.

---

### `NodeConfiguration.java`

This file is a configuration class for Spring:
- The annotation `@ComponentScan` indicates to Spring the need to scan packages to find components and beans.
- The annotation `@PropertySource` declares an external properties file (app.properties) to be loaded into the Spring environment.

**Task:** This class sets up the configuration for the Spring application by defining where Spring should scan for components and which properties file should be used for configuration values. It is used when registering a bot.

---

### `ReactiveDatabaseConfig.java`

This class configures a reactive connection to a PostgreSQL database using R2DBC and manages transactions.

- **Annotation `@EnableR2dbcRepositories`:** is used to enable and configure the creation of reactive repositories based on Spring Data R2DBC.
- **The `ConnectionFactory()` method:** Creates a reactive connection factory (`ConnectionFactory') for working with PostgreSQL via R2DBC.

**Task:** Setting up a reactive connection to a PostgreSQL database.

---

### `SecurityConfig.java`

This class configures the password encryption mechanism using BCrypt.

- **Annotation `@Configuration`:** Defines the class as a configuration class.
- **The `PasswordEncoder()` method:** Returns the `PasswordEncoder` bin, which is used to encrypt passwords using the BCrypt algorithm.

**Task:** Securing passwords through encryption using BCrypt.

---

### `TomcatConfigg.java`

This class configures and runs the embedded Tomcat server.

- **Annotation `@Configuration`:** Defines the class as a configuration class.
- **The `tomcat()` method:** Configures and starts the Tomcat server on the specified port.

**Task:** Start the application on the specified port using the embedded Tomcat server.

---

## Node/dao

### Description of the work of the class `UserDao.java `:

The `UserDao` class is a Data Access Object (DAO) that provides reactive access to user data, preferences, allergies, and restaurant visits using R2DBC and repositories.

### Main responsibilities:
- Manage users in the database (create, search, delete).
- Handle user preferences and allergies.
- Manage user restaurant visit records.

### Fields:
- **`UserRepository userRepository`:** Repository for user data management.
- **`PreferencesRepository preferencesRepository`:** Repository for managing user preferences.
- **`AllergiesRepository allergiesRepository`:** Repository for managing user allergies.
- **`VisitRepository visitRepository`:** Repository for managing user restaurant visits.

### Methods:

 **`saveUser(User user)`**
   - Saves a new User entity in the database using the UserRepository.

**`findUserByUsername(String username)`**
   - Retrieves a User by their username from the database.

**`findUserByEmail(String email)`**
   - Retrieves a User by their email from the database.

**`getAllUserNames()`**
   - Returns a stream of all usernames from the database.

**`saveUserPreference(Long userId, String preference)`**
   - Saves a user's preference in the user_preferences table.

**`findPreferencesByUserId(Long userId)`**
   - Retrieves all preferences associated with a specific user ID.

**`deleteUserPreference(Long userId, String preference)`**
   - Deletes a user's preference by user ID and preference name.

**`saveUserAllergy(Long userId, String allergy)`**
   - Saves a user's allergy in the allergies table.

**`findAllergiesByUserId(Long userId)`**
   - Retrieves all allergies associated with a specific user ID.

**`deleteUserAllergy(Long userId, String allergy)`**
   - Deletes a user's allergy by user ID and allergy name.

**`saveVisit(Long userId, String restaurantId)`**
   - Saves a user's restaurant visit in the visits table with `visited = false`.

**`findVisitsByUserId(Long userId)`**
   - Retrieves all visits associated with a specific user ID, including restaurant ID and visit status.

**`updateVisitStatus(Long userId, String restaurantId, boolean visited)`**
   - Updates the visited status of a restaurant visit for a user by their ID and restaurant ID.

**`deleteVisit(Long userId, String restaurantId)`**
   - Deletes a record of a restaurant visit for a user by their ID and restaurant ID.


### The main task:
The `UserDao` class implements database access to manage users, their preferences, allergies, and restaurant visits. All operations are performed asynchronously using the reactive approach provided by Spring R2DBC.

---

## Node/entity

These classes are entities that will be mapped to the corresponding tables in the database. They are used to work with data in the application.

---

### `AcceptableAllergy.java`

**Table:** `acceptable_allergies`  

- **Fields:**
- `id` is the unique identifier of the allergy.
  - `allergy' is the name of an allergy.

---

### `Allergy.java`

**Table:** `user_allergies`  
**Description:** Stores data about users' allergies, linking the user and their allergies.

- **Fields:**
- `id` is the unique identifier of the record.
  - `userId` is the ID of the user who added the allergy.
  - `allergy' is the name of an allergy.

---

### `AvailablePreference.java`

**Table:** `available_preferences`  

- **Fields:**
- `id` is the unique identifier of the preference.
  - `preference' is the name of the preference.

---


###  `Preference.java`:

This class represents the "preference" entity and is mapped to the `user_preferences` table in the database. This table stores information about user preferences and their relationship to specific users.

### Fields:

- **`id`** is the unique identifier of the record (preferences). The field is annotated as `@Id`, which indicates that it is the primary key in the table.
- **`userId`** is the ID of the user to whom this preference applies. This field indicates a connection with the user.
- **`preference`** is the name of the preference. For example, it may be a category, type of product or service that the user is interested in.

### The main task:

The `Preference` class is used to store and manage data about user preferences. The entity associates the user with specific preferences and stores this data in the 'user_preferences` table.

###  **`User.java`**

**Table:** `users`  
**Description:** Represents the user of the system.

- **Fields:**
- `id` is a unique user ID.
  - `username` is the user's name.
  - `password' is a hashed password.
  - `email` — the user's email address.

- **Constructors:**
Is the default constructor for frameworks such as Spring.
  - A complete constructor for creating users with specified fields.

---

###  `Visit.java`

**Description:** Represents the user's visit to the restaurant.

- `id` — the unique identifier of the visit record (Long).
  - `userId` — the unique identifier of the user (Long).
  - `restaurantId` — the unique identifier of the restaurant (String).
  - `visited` — the status of the visit (Boolean value).

- **Constructors:**
- Constructor for initializing fields.

---

### The main task:
These entities are mapped to the corresponding tables in the database, providing a link between the objects in the application and their representation in the database.

---

## Node/service/impl

`ConsumeServiceImpl.java and ProducerServiceImpl.java `:

These classes implement interaction with message outlines (RabbitMQ) in the system, organizing the processing of incoming messages (consummation) and sending responses (production).

---

###  `ConsumeServiceImpl.java`

This class implements the 'ConsumerService` interface and is responsible for processing incoming messages from various RabbitMQ descriptions.

- **Annotation `@Service`:** Indicates that the class is a Spring component and can be automatically detected and managed by the Spring container.
- **Annotation `@Log4j2`:** Adds the ability to log using Log4j2.

#### Fields:
- **`ProducerService producerService`:** This field stores a link to the service for sending reply messages.

#### Method:

 **`consumeTextMessageUpdates(Update update)`**
- Annotation '@RabbitListener (queues = TEXT_MESSAGE_UPDATE)` allows this method to subscribe to the description `TEXT_MESSAGE_UPDATE'.
   - The method processes text messages received via RabbitMQ and logs the result.
   - After processing, a response message is generated, which is sent via the `ProducerService'.

**`consumeDocMessageUpdates(Update update)`**
- Annotation '@RabbitListener (queues = DOC_MESSAGE_UPDATE)` allows you to subscribe to the description of `DOC_MESSAGE_UPDATE'.
   - The method is responsible for processing documents, but so far its implementation is empty (only receiving a message is logged).

 **`consumePhotoMessageUpdates(Update update)`**
- Annotation '@RabbitListener (queues = PHOTO_MESSAGE_UPDATE)` allows you to subscribe to the description of `PHOTO_MESSAGE_UPDATE'.
   - The method processes photo messages, logging their receipt (the implementation is being deleted for now).

#### The main task:
The 'ConsumeServiceImpl` class is responsible for consuming messages from RabbitMQ essays and processing them depending on the type (text, document, photo). For text messages, the user's response is sent via the 'produserAnswer` method.

---

### `ProducerServiceImpl.java`

This class implements the 'ProducerService` interface and is responsible for sending messages to the RabbitMQ essay.

- **Annotation `@Service`:** The class is located by the Spring component and is managed by the Spring container.
- **Annotation `@Log4j2`:** Adds support for logging using Log4j2.

#### Fields:
- **`RabbitTemplate RabbitTemplate`:** Used to send messages to RabbitMQ. The field is passed through the constructor using the `@Autowired` annotation.

#### Method:

**`produserAnswer(SendMessage sendMessage)`**
- This method sends a response message to the `ANSWER_MESSAGE` queue using the `RabbitTemplate`.
   - **`rabbitTemplate.convertAndSend (ANSWER_MESSAGE, sendMessage)`:** Sends the `SendMessage` object to the specified characteristic.

#### The main task:
`ProducerServiceImpl` implements sending messages to the RabbitMQ essay. This class is used to send reply messages to users after processing their incoming messages.

---

### Interaction:
- **'ConsumeServiceImpl`** is responsible for receiving messages from various essays and processing them. For example, when receiving a text message from the `TEXT_MESSAGE_UPDATE` queue, it uses the `ProducerService` to send a reply message.
- **`ProducerServiceImpl`** sends response messages to the `ANSWER_MESSAGE` description so that other system components can receive and process them.

Thus, both classes work together to provide asynchronous communication through RabbitMQ.

---

## Node/service

These classes manage logic related to users, preferences, allergies, sessions, and interactions with external APIs such as Foursquare.

---

###  `AllergyLoaderService.java`

**Task:** Loads allergies from a file and initializes the 'acceptable_allergies` table in the database.

- **The `init()`** method is called when the component is initialized. Recreates the table and loads data from the file `allergies.txt `.
- **The `recreateTable()` method** — recreates the 'acceptable_allergies` table.
- **The `loadAllergiesFromFile()' method** — reads data from a file and stores it in a database.

---

###  `FoursquareService.java`

**Task:** Interact with the Foursquare API to search for restaurants.

- **The `searchRestaurants()` method** — generates a query to the Foursquare API to search for restaurants according to the specified parameters (location, keywords, sorting, price, etc.).

---

###  `PreferencesLoaderService.java`

**Task:** Loads preferences from a file and initializes the `available_preferences` table in the database.

- **The 'init()` method** — is called during component initialization, recreates the table and loads data from the file `preferences.txt `.
- **The `recreateTable()` method** — recreates the `available_preferences` table.
- **The `loadPreferencesFromFile()' method** — reads data from a file and stores it in a database.

---

###  `SessionService.java`

**Task:** Manages user sessions.

- **The `isUserLoggedIn()` method** — checks whether the user is logged in based on the `chatId'.
- **The `LoginUser()` method** — registers the user as authorized.
- **The `logOutUser()` method** — removes the user from the list of authorized users.
- **The `getUserId()` method** — returns the user ID by `chatId`.

---

### `UserService.java`

**Task:** Manages user data, preferences, allergies, and restaurant visits. Interacts with repositories and services to process requests.

- **Methods for managing users:**
  - **`addUser()`** — adds a new user to the system.
  - **`authenticate()`** — verifies the user's login credentials.
  - **`getUserIdByUsername()`** — retrieves the user ID based on their username.
  - **`getAllUsers()`** — retrieves the list of all usernames in the system.

- **Methods for managing preferences:**
  - **`addUserPreference()`** — adds a preference for the user.
  - **`getUserPreferences()`** — retrieves the list of user preferences.
  - **`deleteUserPreference()`** — deletes a user's specific preference.

- **Methods for managing allergies:**
  - **`addUserAllergy()`** — adds an allergy for the user.
  - **`getUserAllergies()`** — retrieves the list of user allergies.
  - **`deleteUserAllergy()`** — deletes a specific allergy for the user.

- **Methods for managing visits:**
  - **`addVisit()`** — adds a restaurant visit for the user.
  - **`getVisitList()`** — returns the list of user visits.
  - **`markVisited()`** — marks the restaurant as visited.
  - **`removeVisit()`** — deletes the record of the restaurant visit.


---

### The main task:
Classes provide various services for working with users, preferences, allergies, restaurant visits, as well as interacting with external APIs.


---

## Resource:
The resource files store default values for allergies and preferences, as well as data for accessing the broker, database, api, and so on.

---
