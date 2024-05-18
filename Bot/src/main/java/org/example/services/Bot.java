package org.example.services;

import org.example.configuration.BotConfiguration;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

@Component
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration config;
    private AnnotationConfigApplicationContext context;


    @Autowired
    public Bot(BotConfiguration botConfig, AnnotationConfigApplicationContext context){
        this.config = botConfig;
        this.context = context;
    }



   // @Override
    //public String getBotPath() {
     //   return config.getUrl();
    //}

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            sendAnswerMessage(chatId,"Hello<3!");
        }
    }

    public void sendAnswerMessage(long chatId, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}