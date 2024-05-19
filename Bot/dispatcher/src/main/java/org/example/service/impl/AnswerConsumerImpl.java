package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.controller.UpdateController;
import org.example.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.example.model.RabbitQueue.ANSWER_MESSAGE;

//чтобы считать из брокера ответы, которые были отправлены из ноды
@Service
@Log4j2
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
        log.info("Answer Consumer in dispatcher");
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
        log.info("consume in dispatcher");
    }
}
