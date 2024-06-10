# Telegram_Restaurant_Bot

## Overview

The Dispatcher Bot Application is a Telegram bot designed to handle various user commands, interact with a RabbitMQ message broker(interaction with the message broker is configured, but is not used directly for messaging, this item will be configured in the near future), and integrate with a Foursquare service to provide restaurant recommendations. The bot allows users to register, login, add preferences and allergies, and find restaurants based on their preferences. The application consists of several components, including a bot service, message processing, and interaction with a PostgreSQL database.

## Features

- User Registration and Login: Users can register and log in to the bot.
- Preferences and Allergies Management: Users can add, view, and delete food preferences and allergies.
- Restaurant Search: Users can search for restaurants based on location, cuisine, and other criteria.
- Visit List Management: Users can add restaurants to their visit list, mark them as visited, and remove them.

## Commands

### General Commands

- /start: Show the main menu.
- /help: Display a list of available commands.

### User Management

- /register [username] [password] [email]: Register a new user.
- /login [username] [password]: Log in to the bot.
- /logout: Log out from the bot.

### Preferences Management

- /addpref [preference]: Add a new food preference.
- /viewprefs: View all added preferences.
- /delpref [preference]: Delete a specified preference.

### Allergies Management

- /addallergy [allergy]: Add a new allergy.
- /viewallergies: View all added allergies.
- /delallergy [allergy]: Delete a specified allergy.

### Restaurant Search

- /findrestaurant [location], [categoryID], [keywords], [skipCategories]: Find restaurants based on location and other criteria.
- /randomrestaurant [location] [radius]: Find a random restaurant in the specified area.

### Visit List Management

- /visitlist: Show available commands for managing the visit list.
- /addvisit [restaurant_id]: Add a restaurant to the visit list.
- /showlist: Show all restaurants in the visit list.
- /markvisited [restaurant_id]: Mark a restaurant as visited.
- /removevisit [restaurant_id]: Remove a restaurant from the visit list.

## Running the Bot

- The application is launched through the Dispatcher Application class. After startup, a conclusion appears about which port the application is running on and about successfully created queues and values for tables from the default databases.
- After starting the application, the bot will be initialized and registered with Telegram using the provided bot token and username.
- The bot will listen for incoming messages and process them according to the defined commands.
