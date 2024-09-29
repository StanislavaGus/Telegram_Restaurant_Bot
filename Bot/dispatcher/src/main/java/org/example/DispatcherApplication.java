package org.example;

import org.example.configuration.BotInitialization;
import org.example.configuration.DispatcherConfiguration;
import org.node.configuration.NodeConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DispatcherApplication {

    public static void main(String[] args) throws TelegramApiException {
        // Инициализация контекста с профилями dispatcher и node
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(DispatcherConfiguration.class, NodeConfiguration.class);
        context.refresh();

        // Инициализация бота
        context.getBean(BotInitialization.class).init();
    }
}
