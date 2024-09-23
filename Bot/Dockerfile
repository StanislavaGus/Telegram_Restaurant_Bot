# Используем официальный образ OpenJDK как базовый
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем исходный код или скомпилированный JAR файл в контейнер
COPY target/DispatcherApplication.jar /app/DispatcherApplication.jar

# Указываем порт, который приложение будет использовать
EXPOSE 8084

# Команда для запуска приложения
CMD ["java", "-jar", "DispatcherApplication.jar"]
