-- init-db.sql

-- 1) Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL
    );

-- 2) Таблица визитов
CREATE TABLE IF NOT EXISTS visits (
                                      id SERIAL PRIMARY KEY,
                                      user_id INTEGER NOT NULL,
                                      restaurant_id VARCHAR(255) NOT NULL,
    visited BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- 3) Допустимые аллергии
CREATE TABLE IF NOT EXISTS acceptable_allergies (
                                                    id SERIAL PRIMARY KEY,
                                                    allergy VARCHAR(255) NOT NULL
    );

-- 4) Доступные предпочтения
CREATE TABLE IF NOT EXISTS available_preferences (
                                                     id SERIAL PRIMARY KEY,
                                                     preference VARCHAR(255) NOT NULL
    );

-- 5) Связь пользователь–аллергии
CREATE TABLE IF NOT EXISTS user_allergies (
                                              id SERIAL PRIMARY KEY,
                                              user_id INTEGER NOT NULL,
                                              allergy VARCHAR(250) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- 6) Связь пользователь–предпочтения
CREATE TABLE IF NOT EXISTS user_preferences (
                                                id SERIAL PRIMARY KEY,
                                                user_id INTEGER NOT NULL,
                                                preference VARCHAR(250) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );