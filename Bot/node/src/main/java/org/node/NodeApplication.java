package org.node;

import org.node.configuration.RabbitMQConfig1;
import org.node.configuration.RabbitConfiguration1;
import org.node.configuration.TomcatConfig1;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NodeApplication {
    public static void main(String[] args) {
        // Инициализация контекста Spring с использованием всех необходимых классов конфигурации
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                RabbitMQConfig1.class,
                RabbitConfiguration1.class,
                TomcatConfig1.class
        );

        // Убедитесь, что сервер Tomcat запускается
        context.getBean(TomcatConfig1.class);
    }
}
