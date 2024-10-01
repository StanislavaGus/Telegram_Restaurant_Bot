package org.node.listener;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class DatabaseConnectionChecker implements ApplicationListener<ContextRefreshedEvent> {

    private final ConnectionFactory connectionFactory;

    public DatabaseConnectionChecker(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Mono.from(connectionFactory.create())
                .doOnSuccess(connection -> {
                    log.info("Successfully connected to the database");
                    connection.close();
                })
                .doOnError(error -> log.error("Error while connecting to the database", error))
                .subscribe();
    }
}
