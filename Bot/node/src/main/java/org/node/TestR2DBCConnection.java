package org.node;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import reactor.core.publisher.Mono;

public class TestR2DBCConnection {
    public static void main(String[] args) {
        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(32768)
                .username("userok")
                .password("12345")
                .database("postgres")
                .build();

        PostgresqlConnectionFactory factory = new PostgresqlConnectionFactory(config);

        Mono.from(factory.create())
                .flatMap(connection -> Mono.from(connection.createStatement("SELECT 1").execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, Integer.class))))
                        .doFinally(signal -> connection.close()))
                .doOnNext(System.out::println)
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }
}

