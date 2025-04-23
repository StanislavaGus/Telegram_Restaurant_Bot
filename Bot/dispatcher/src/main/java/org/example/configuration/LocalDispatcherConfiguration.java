package org.example.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("local")
@ComponentScan(basePackages = {"org.example"})
@PropertySource("classpath:aplication-local.properties")
public class LocalDispatcherConfiguration {
}
