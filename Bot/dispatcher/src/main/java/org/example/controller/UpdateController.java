package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.example.service.UpdateProducer;
import org.example.communication.Bot;
import org.example.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Component
@Log4j2
public class UpdateController {
    private Bot bot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;
    public UpdateController(MessageUtils messageUtils,UpdateProducer updateProducer){
        this.messageUtils=messageUtils;
        this.updateProducer=updateProducer;
    }


    public void registerBot(Bot bot){
        this.bot=bot;
    }

    public void processUpdate(Update update){
        if (update==null){
            log.error("Received update is null");
            return;
        }

        if (update.getMessage()!=null){
            distributeMessagesByType(update);
        }else{
            log.error("Received unsupported message type"+update);
        }

    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Вы прислали недопустимый файл! Воспользуйтесь командой \\help");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        bot.sendAnswerMessage(sendMessage);
    }


    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}