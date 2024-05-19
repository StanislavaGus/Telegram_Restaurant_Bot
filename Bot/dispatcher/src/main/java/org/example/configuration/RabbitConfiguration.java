package org.example.configuration;


import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;

import static org.example.model.RabbitQueue.*;


@Configuration
@Log4j2
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConvertor(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    @Bean
    public Queue textMessageQueue(){
        log.info("Creating text_message_update queue");
        return new Queue(TEXT_MESSAGE_UPDATE);
    }
    @Bean
    public Queue docMessageQueue(){
        log.info("Creating doc_message_update queue");
        return new Queue(DOC_MESSAGE_UPDATE);
    }
    @Bean
    public Queue photoMessageQueue(){
        log.info("Creating photo_message_update queue");
        return new Queue(PHOTO_MESSAGE_UPDATE);
    }
    @Bean
    public Queue answerMessageQueue(){
        return new Queue(ANSWER_MESSAGE);
    }
}
