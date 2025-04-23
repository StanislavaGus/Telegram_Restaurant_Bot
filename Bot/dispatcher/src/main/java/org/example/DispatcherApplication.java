package org.example;

import org.example.configuration.BotInitialization;
import org.example.configuration.DockerDispatcherConfiguration;
import org.node.configuration.NodeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication(scanBasePackages = {"org.example","org.node"})
public class DispatcherApplication {

//    public static void main(String[] args) throws TelegramApiException {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
////        context.register(DispatcherConfiguration.class, NodeConfiguration.class);
////        context.refresh();
//
//// 1) Указываем в рантайме, какой профиль активен
//        context.getEnvironment().setActiveProfiles("docker");
//
//// 2) Регистрируем ваши конфиги
//        context.register(DockerDispatcherConfiguration.class, NodeConfiguration.class);
//
//// 3) Инициализируем контекст
//        context.refresh();
//
//        // Инициализация бота
//        context.getBean(BotInitialization.class).init();
//    }
    public static void main(String[] args) {
        SpringApplication.run(DispatcherApplication.class, args);
}

}
