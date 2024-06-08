package org.example.configuration;

import org.example.services.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Component
public class BotInitialization {
    private final BotConfiguration botConfig;
    private AnnotationConfigApplicationContext context;

    @Autowired
    public BotInitialization(BotConfiguration botConfig, AnnotationConfigApplicationContext context){
        this.botConfig = botConfig;
        this.context = context;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        Bot bot = context.getBean(Bot.class);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}