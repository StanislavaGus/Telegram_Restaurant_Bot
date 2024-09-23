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


## Run the application 

1. **Install RabbitMQ**: Download and install RabbitMQ from the official website. Ensure that the RabbitMQ service is running after installation.

2. **Install PostgreSQL**: Download and install PostgreSQL from the official website. After installation, create the necessary databases and users for your application.

3. **Run the application**: After configuring RabbitMQ and PostgreSQL, navigate to the project directory and run the `DispatcherApplication` file to start the application. This will initialize the Spring context and connect to the required services.


# Using example

<img src="https://github.com/user-attachments/assets/d7345348-2dde-4daa-a56c-347b0e669434" width="300" />

### Registration
<img src="https://github.com/user-attachments/assets/ac13a708-b049-4b9a-82a3-a56a33f6a7d5" width="300" />

### Login

After a successful login, the message with the password is deleted from the chat.

<img src="https://github.com/user-attachments/assets/0ad7cafb-f568-4b95-8233-d73f402ba9ee" width="300" />

<img src="https://github.com/user-attachments/assets/8fa1d96f-dab7-4769-a138-5b0a2f18ec1b" width="300" />

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

## Node/cofiguration

###  `DatabaseConfig.java`

This class configures the connection to the PostgreSQL database using JDBC and provides the `JdbcTemplate` bean for executing SQL queries.

- **Annotation `@Configuration`:** Indicates that this is the Spring configuration class.
- **Annotation `@Value`:** Used to embed values from the properties file ('application.properties').
- **The `dataSource()` method:** Creates and configures a data source (`DataSource') to connect to the database.
- **The 'JdbcTemplate()` method:** Creates the 'JdbcTemplate` bean, which will be used to execute SQL queries.

**Task:** Managing the connection to the PostgreSQL database and creating a 'JdbcTemplate` for working with SQL.

---

### `FoursquareConfig.java`

This class configures the HTTP client to work with the Foursquare API and provides an API key for interacting with the service.

- **Annotation `@Configuration`:** Defines the class as a configuration class.
- **The `OkHttpClient()` method:** Returns the 'OkHttpClient` bin for executing HTTP requests.
- **The `foursquareApiKey() method`:** Returns the API key for accessing Foursquare.

**Task:** Configuring the HTTP client to work with the Foursquare API and providing an API key.

---

### `NodeConfiguration.java`

This class is responsible for configuring components and repositories specific to the `node` profile.

- **Annotation `@Profile("node")`:** Indicates that this configuration is activated only when using the `node` profile.
- **Annotation `@ComponentScan`:** Scans packages to find Spring components.
- **Annotation `@EnableR2dbcRepositories`:** Includes support for reactive repositories for working with the database via R2DBC.

**Task:** Managing components and repositories specific to the `node` profile.

---

### `ReactiveDatabaseConfig.java`

This class configures a reactive connection to a PostgreSQL database using R2DBC and manages transactions.

- **Annotation `@EnableTransactionManagement`:** Enables transaction management.
- **The `ConnectionFactory()` method:** Creates a reactive connection factory (`ConnectionFactory') for working with PostgreSQL via R2DBC.
- **The `databaseClient()` method:** Returns `DatabaseClient`, which is used to make queries to the database.
- **The `r2dbcEntityTemplate()` method:** Provides a reactive template for working with entities.
- **The `TransactionManager()` method:** Creates a transaction manager to manage reactive transactions.
- **The `transactionalOperator()` method:** Manages transactions through the `TransactionalOperator'.

**Task:** Setting up a reactive connection to a PostgreSQL database with transaction support.

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
- **Annotation `@ComponentScan`:** Scans packages to find Spring components.
- **Annotation `@PropertySource`:** Loads configuration values from the 'appp.properties` file.
- **The `tomcat()` method:** Configures and starts the Tomcat server on the specified port.

**Task:** Configuring and running the built-in Tomcat server for the application to work.

---

## Node/controller

The `UserController` class is a Spring controller that provides a REST API for managing users and searching for restaurants using the Foursquare service.

### The main tasks of the class:
- User management: adding users, getting a list of all users, authentication.
- Search for restaurants via the Foursquare API.

### Methods:

**`addUser`**
- **Method:** `POST /addUser`
   - **Parameters:** `username`, `password`, `email`
   - **Return value:** `Mono<Void>`
   - **Description:** This method adds a new user to the system. Uses the `UserService` service to save user information (asynchronously, using `Mono').

 **`getAllUsers`**
- **Method:** `GET /allUsers`
   - **Return value:** `Flux<String>`
- **Description:** Returns the stream of all users of the system. Uses `UserService` to get a list of users (asynchronously, using `Flux').

 **`login`**
- **Method:** `POST /login`
   - **Parameters:** `username`, `password`
   - **Return value:** `Mono<Boolean>`
   - **Description:** Authenticates the user by name and password. Returns `true` or `false` depending on authentication success (asynchronously).

**`searchRestaurants`**
- **Method:** `GET /searchRestaurants`
   - **Parameters:**
     - `location' (required): The location to search for.
     - `keywords' (optional): keywords for filtering restaurants.
     - `sort' (optional, "RELEVANCE" by default): sorting criteria.
     - `openNow' (optional, default is `false'): filtering by the status "open now".
     - `maxPrice' (optional, default 4): maximum price.
     - `latitude' (optional): the latitude of the location.
     - `longitude' (optional): the longitude of the location.
   - **Return value:** `Mono<JsonNode>`
   - **Description:** Searches for restaurants using the `FoursquareService` service based on the passed parameters. Returns results in JSON format (asynchronously).

### Interaction with services:
- **`UserService`:** Handles user-related operations such as adding, authenticating, and retrieving a list of users.
- **`FoursquareService`:** Responsible for interacting with the Foursquare API for restaurant search.

### The main task:
The `UserController` class implements a controller for managing users and searching for restaurants, providing several REST API endpoints for working with users and making requests to an external service.

---

## Node/dao

### Description of the work of the class `UserDao.java `:

The `UserDao' class is a DAO (Data Access Object) that provides access to user data, preferences, allergies, and restaurant visits using reactive approaches and SQL queries via `R2dbcEntityTemplate` and `UserRepository'.

### The main tasks of the class:
- User management in the database (creation, search, deletion).
- Manage user preferences and allergies.
- Management of data on restaurant visits by users.

### Fields:
- **`R2dbcEntityTemplate r2dbcEntityTemplate`:** Used to execute SQL queries using the reactive API.
- **`UserRepository userRepository`:** A repository for managing user data.

### Methods:

 **`saveUser(User user)`**
   - Saves the new user in the database.
   - Uses `UserRepository` to save the `User` object.

 **`findUserByUsername(String username)`**
- Searches for a user by username.
   - Executes an SQL query for the search and converts the result into a 'User` object.

 **`findUserByEmail(String email)`**
   - Searches for a user by email.
   - Executes an SQL query for the search and converts the result into a 'User` object.

 **`getAllUserNames()`**
- Returns a stream of names of all users.
   - Executes an SQL query to get a list of all user names (username).

 **`saveUserPreference(Long userId, String preference)`**
- Saves the user's preference to the `user_preferences` table.

 **`findPreferencesByUserId(Long userId)`**
   - Returns all user preferences by their ID.
   - Executes an SQL query to search for all user preferences.

 **`deleteUserPreference(Long userId, String preference)`**
- Deletes the user's preference by ID and preference.

 **`saveUserAllergy(Long userId, String allergy)`**
   - Saves the user's allergy to the `allergies` table.

 **`findAllergiesByUserId(Long userId)`**
   - Returns all the user's allergies by their ID.

 **`deleteUserAllergy(Long userId, String allergy)`**
    - Removes the user's allergy by his ID and the name of the allergy.

 **`saveVisit(Long userId, String restaurantId)`**
    - Saves a record of the user's visit to the restaurant in the `visits` table, with the flag `visited = false'.

 **`findVisitsByUserId(Long userId)`**
    - Returns a list of the user's restaurant visits by their ID, including the restaurant ID and the visit status.

 **`updateVisitStatus(Long userId, String restaurantId, boolean visited)`**
    - Updates the restaurant visit status (true/false) for the user.

 **`deleteVisit(Long userId, String restaurantId)`**
    - Deletes the record of a restaurant visit for the user by his ID and restaurant ID.

### The main task:
The `UserDao` class implements database access to manage users, their preferences, allergies, and restaurant visits. All operations are performed asynchronously using the reactive approach provided by Spring R2DBC.

---

## Node/entity

These classes are entities that will be mapped to the corresponding tables in the database. They are used to work with data in the application.

---

### `AcceptableAllergy.java`

**Table:** `acceptable_allergies`  
**Description:** Provides a list of acceptable (allowed) allergies that can be selected by users or added to the system.

- **Fields:**
- `id` is the unique identifier of the allergy.
  - `allergy' is the name of an allergy.

- **Methods:**
- Getters and setters for both fields.

---

### `Allergy.java`

**Table:** `user_allergies`  
**Description:** Stores data about users' allergies, linking the user and their allergies.

- **Fields:**
- `id` is the unique identifier of the record.
  - `userId` is the ID of the user who added the allergy.
  - `allergy' is the name of an allergy.

- **Methods:**
- Getters and setters for all fields.

---

### `AvailablePreference.java`

**Table:** `available_preferences`  
**Description:** Provides a list of available preferences that can be selected by users.

- **Fields:**
- `id` is the unique identifier of the preference.
  - `preference' is the name of the preference.

- **Methods:**
- Getters and setters for both fields.

---


###  `Preference.java`:

This class represents the "preference" entity and is mapped to the `user_preferences` table in the database. This table stores information about user preferences and their relationship to specific users.

### Fields:

- **`id`** is the unique identifier of the record (preferences). The field is annotated as `@Id`, which indicates that it is the primary key in the table.
- **`userId`** is the ID of the user to whom this preference applies. This field indicates a connection with the user.
- **`preference`** is the name of the preference. For example, it may be a category, type of product or service that the user is interested in.

### Methods:

- **Getters and setters:**
- `getId()` / `setId(Long id)` — getting and setting the value for the `id` field.
  - `getUserId()` / `setUserId(Long userId)` — getting and setting the value for the `userId` field.
  - `getPreference()` / `setPreference(String preference)` — getting and setting the value for the `preference` field.

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

- **Methods:**
- Getters and setters for all fields.
  - Redefined methods `toString()`, `equals()`, and `hashCode()` to work correctly with user objects.

---

###  `Visit.java`

**Description:** Represents the user's visit to the restaurant.

- **Fields:**
- `restaurantId` is the unique identifier of the restaurant.
  - `visited` — the status of the visit (Boolean value).

- **Constructors:**
- Constructor for initializing fields.

- **Methods:**
- Getters for the `restaurantId` and `visited` fields.

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

**Task:** Interact with the Foursquare API to search for restaurants and get information about them.

- **The `searchRestaurants()` method** — generates a query to the Foursquare API to search for restaurants according to the specified parameters (location, keywords, sorting, price, etc.).
- **The `searchRandomRestaurant()` method** — executes a query to search for a random restaurant.
- **The `getRestaurantLink()` method** — gets a link to a restaurant by its 'fsqId` via the Foursquare API.

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

- **Methods for working with users:**
  - **`addUser()`** — adds a new user, checking the uniqueness of the email and username.
  - **`authenticate()`** — verifies user authentication by username and password.
  - **`getUserIdByUsername()`** — returns the user ID by name.
  - **`getAllUsers()`** — returns a list of all users.

- **Methods for working with preferences:**
- **`addUserPreference()`** — adds the user's preference, checking if it has already been added.
  - **`getUserPreferences()`** — returns all user preferences.
  - **`deleteUserPreference()`** — deletes the user's preference.

- **Methods for dealing with allergies:**
- **`addUserAllergy()`** — adds an allergy to the user, checking if it has already been added.
  - **`getUserAllergies()`** — returns all the user's allergies.
  - **`deleteUserAllergy()`** — removes the user's allergy.
  - **`isAcceptableAllergy()`** — checks whether the allergy is acceptable (whether it is present in the `acceptable_allergies` table).

- **Methods for working with visits:**
- **`addVisit()`** — adds a restaurant visit for the user.
  - **`getVisitList()`** — returns the list of user visits.
  - **`markVisited()`** — marks the restaurant as visited.
  - **`removeVisit()`** — deletes the record of the restaurant visit.

- **Methods for working with restaurants via the Foursquare API:**
- **`findRestaurant()`** — searches for restaurants.
  - **`requestRandomRestaurant()`** — searches for a random restaurant.

---

### The main task:
Classes provide various services for working with users, preferences, allergies, restaurant visits, as well as interacting with external APIs.

---
## Node/NodeApplication.java

The 'NodeApplication` class is the entry point for launching a Spring application that uses several configuration classes to configure various components such as RabbitMQ, Tomcat, and a database. This class manually initializes the Spring context using the `AnnotationConfigApplicationContext'.

### Basic elements:

 **`AnnotationConfigApplicationContext`**:
- This class is used to create a Spring context based on Java configurations.
   - Configuration classes are passed to the constructor arguments, which will be used to configure the application components.

**Configuration classes:**
- **`RabbitMQConfigg`** — configuration for RabbitMQ.
   - **`RabbitConfigurationn`** — additional configuration for RabbitMQ.
   - **`TomcatConfigg`** — configuration for the embedded Tomcat server.
   - **`DatabaseConfig`** — configuration for connecting to a database using JDBC.
   - **`ReactiveDatabaseConfig`** — a reactive configuration for connecting to a database using R2DBC.

 **Starting Tomcat**:
- After initializing the context, the `TomcatConfigg` bin is extracted to make sure that the Tomcat server is properly configured and running.

### Tasks:

**Initializing the Spring context**:
- The application uses several configuration classes to configure databases, RabbitMQ and the embedded Tomcat server.
   - `AnnotationConfigApplicationContext` initializes all these classes and components in the Spring context.

 **Launching Tomcat**:
   - In the last line, the `TomcatConfigg` bean is extracted, which indicates that the application runs the embedded Tomcat server if it has been configured correctly.

### The main task:
The 'NodeApplication` class launches the application by creating a Spring context and configuring key components such as RabbitMQ, database, and Tomcat server using Java configurations.

---

## Resource:
The resource files store default values for allergies and preferences, as well as data for accessing the broker, database, api, and so on.

---
