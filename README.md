# Telegram_Restaurant_Bot






# Realisation
## Bot.java
This file contains an implementation of a Telegram bot that interacts with users, accepts commands and performs actions based on these commands.

 **onUpdateReceived(Update update)**  
   The main method of processing incoming messages and updates from Telegram. Determines which type of message was received (text, location, etc.), and causes appropriate processing depending on the content. Controls the logic of executing commands such as `/start`, `/help`, `/addpref`, `/delallergy`, as well as actions for sending and processing the user's location.

  **sendCustomMenu(long chatId, List<String> buttonNames, int buttonsPerRow)**
Creates a custom menu with buttons that is sent as a message to the user. The buttons are organized into rows according to the specified number of buttons per row.



### List of places

**handleAddVisitCommand(long chatId, String messageText)**  
   Adds a restaurant to the user's list of visits. Checks if the user is logged in and adds a new restaurant to his list of visits if there is no such restaurant there yet.

**handleShowListCommand(long chatId)**  
   Displays a list of restaurants added to the user's "list of visits". If a restaurant has been marked as visited, its link is displayed using HTML markup.

 **handleMarkVisitedCommand(long chatId, String messageText)**  
   Marks the restaurant as visited in the user's list of visits. If the restaurant is not found, a corresponding message is displayed.

**handleRemoveVisitCommand(long chatId, String messageText)**  
   Removes a restaurant from the user's list of visits. If the restaurant is not found, a corresponding message is displayed.   

### Preferences

   **handleAddAllergyCommand(long chatId, String messageText)**  
    Adds a user's allergy to their profile. Checks the validity of the allergy and updates the list of allergies for the current user.

 **handleViewAllergiesCommand(long chatId)**  
    Displays a list of the user's allergies. If there are no allergies, a corresponding message is displayed.

 **handleDeleteAllergyCommand(long chatId, String messageText)**  
    Removes the specified allergies from the user's profile.   

### Allergies

 **handleAddPreferenceCommand(long chatId, String messageText)**  
    Adds the user's preferences to their profile. The entered preferences are saved for further use in filtering restaurants.

 **handleViewPreferencesCommand(long chatId)**  
    Displays a list of the user's preferences. If there are no preferences, a corresponding message is displayed.

 **handleDeletePreferenceCommand(long chatId, String messageText)**  
    Deletes the specified preferences from the user's profile.   


### User authorization and data concealment

 **handleLoginCommand(long chatId, String messageText, int messageId)**  
    Processes the login command. If authentication is successful, the user logs in and the message with credentials is deleted.

 **deleteMessage(long chatId, int messageId)**  
Deletes a message that contains login and password by its ID in the chat.

### Search for a restaurant

There are two possible search modes. 

The random restaurant search searches for a random restaurant by a given city and search radius.

**handleRandomRestaurantCommand(long catid, String message Text)**  
    Performs a random search for restaurants by a given location and radius. Uses the user's preferences and allergies to filter the results.

There is also a standard search with parameters. 

 **start Searching(long cat Id)**  
    Searches for restaurants based on the user's entered parameters, such as location, preferences, allergies, pricing policy, whether the restaurant is open now, search radius, etc. The results are displayed as links to restaurants. You can also specify an option in the parameters to sort the results.

## RabbitMQ
The RabbitMQ message broker has been added to the project, however, queues are formed on this version, but are not used. It will be updated in the future.

   
