package org.example.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dispatcher")
@ComponentScan(basePackages = {"org.example"})
public class DispatcherConfiguration {
}

