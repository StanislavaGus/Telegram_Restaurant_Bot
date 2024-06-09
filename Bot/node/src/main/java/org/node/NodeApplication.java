package org.node;

import org.node.configuration.DatabaseConfig;
import org.node.configuration.RabbitMQConfigg;
import org.node.configuration.RabbitConfigurationn;
import org.node.configuration.ReactiveDatabaseConfig;
import org.node.configuration.TomcatConfigg;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NodeApplication {
    public static void main(String[] args) {
        // Инициализация контекста Spring с использованием всех необходимых классов конфигурации
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                RabbitMQConfigg.class,
                RabbitConfigurationn.class,
                TomcatConfigg.class,
                DatabaseConfig.class,
                ReactiveDatabaseConfig.class
        );

        // Убедиться, что сервер Tomcat запускается
        context.getBean(TomcatConfigg.class);
    }
}
