package org.example;

import org.example.configuration.BotConfiguration;
import org.example.configuration.BotInitialization;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DispatcherApplication {

    public static void main(String[] args) throws TelegramApiException {
        // Инициализация контекста
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BotConfiguration.class);

        // Вывод всех бинов
        //String[] beanNames = context.getBeanDefinitionNames();

        //for (String beanName : beanNames) {
          //  Object bean = context.getBean(beanName);
            //System.out.println("Bean name:"+ beanName+" Bean content:"+bean);
      //  }

        // Инициализация бота
        context.getBean(BotInitialization.class).init();
    }

}