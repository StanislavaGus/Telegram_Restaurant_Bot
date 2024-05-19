package org.node;

import org.node.configuration.RabbitMQConfig;
import org.node.configuration.RabbitConfiguration;
import org.node.configuration.TomcatConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NodeApplication {
    public static void main(String[] args) {
        // Инициализация контекста Spring с использованием всех необходимых классов конфигурации
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                RabbitMQConfig.class,
                RabbitConfiguration.class,
                TomcatConfig.class
        );

        // Убедитесь, что сервер Tomcat запускается
        context.getBean(TomcatConfig.class);
    }
}
