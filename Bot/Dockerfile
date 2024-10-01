# Используем официальный образ OpenJDK как базовый
FROM openjdk:21-jdk

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный JAR файл с зависимостями в контейнер
COPY dispatcher/target/dispatcher-1.0-SNAPSHOT-jar-with-dependencies.jar /app/DispatcherApplication.jar

# Явно копируем файл .env в рабочую директорию /app
COPY .env /app/.env

# Указываем порт, который приложение будет использовать
EXPOSE 8088

# Команда для запуска приложения
CMD ["java", "-jar", "DispatcherApplication.jar"]
