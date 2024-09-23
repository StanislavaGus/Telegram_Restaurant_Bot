package org.node.service.impl;

import lombok.extern.log4j.Log4j2;
import org.node.service.ConsumerService;
import org.node.service.ProducerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Service
@Log4j2
public class ConsumeServiceImpl implements ConsumerService {
    private final ProducerService producerService;
    private static FourArray finiteStateMachine;

    public ConsumeServiceImpl(ProducerService producerService) {
        this.producerService = producerService;
        this.finiteStateMachine = new FourArray();

        finiteStateMachine.add(new FourArray.Foure(0,"", 1,""));
        finiteStateMachine.add(new FourArray.Foure(0,"", 2,""));
        finiteStateMachine.add(new FourArray.Foure(0,"", 3,""));
        finiteStateMachine.add(new FourArray.Foure(0,"", 4,""));
        finiteStateMachine.add(new FourArray.Foure(0,"", 5,""));
        finiteStateMachine.add(new FourArray.Foure(1,"", 0,""));
        finiteStateMachine.add(new FourArray.Foure(1,"", 1,""));
        finiteStateMachine.add(new FourArray.Foure(1,"", 1,""));
        finiteStateMachine.add(new FourArray.Foure(1,"", 1,""));
        finiteStateMachine.add(new FourArray.Foure(2,"", 0,""));
        finiteStateMachine.add(new FourArray.Foure(2,"", 2,""));
        finiteStateMachine.add(new FourArray.Foure(2,"", 2,""));
        finiteStateMachine.add(new FourArray.Foure(2,"", 2,""));
        finiteStateMachine.add(new FourArray.Foure(3,"", 0,""));
        finiteStateMachine.add(new FourArray.Foure(3,"", 3,""));
        finiteStateMachine.add(new FourArray.Foure(3,"", 3,""));
        finiteStateMachine.add(new FourArray.Foure(3,"", 3,""));
        finiteStateMachine.add(new FourArray.Foure(4,"", 0,"")); finiteStateMachine.add(new FourArray.Foure(0,"", 1,""));
        finiteStateMachine.add(new FourArray.Foure(4,"", 4,""));
        finiteStateMachine.add(new FourArray.Foure(4,"", 4,""));
        finiteStateMachine.add(new FourArray.Foure(4,"", 4,""));
        finiteStateMachine.add(new FourArray.Foure(5,"", 0,""));
        finiteStateMachine.add(new FourArray.Foure(5,"", 5,""));
        finiteStateMachine.add(new FourArray.Foure(5,"", 6,""));
        finiteStateMachine.add(new FourArray.Foure(6,"", 5,""));
        finiteStateMachine.add(new FourArray.Foure(6,"", 7,""));
        finiteStateMachine.add(new FourArray.Foure(7,"", 0,""));
        finiteStateMachine.add(new FourArray.Foure(7,"", 7,""));
        finiteStateMachine.add(new FourArray.Foure(7,"", 8,""));
        finiteStateMachine.add(new FourArray.Foure(8,"", 7,""));
        finiteStateMachine.add(new FourArray.Foure(8,"", 8,""));
        finiteStateMachine.add(new FourArray.Foure(8,"", 8,""));

    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.info("NODE: Text message is received");

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Invalid text message, call /help to see the valid commands.");
        producerService.produserAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.info("NODE: Doc message is received");

    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.info("NODE: Photo message is received");

    }
}
