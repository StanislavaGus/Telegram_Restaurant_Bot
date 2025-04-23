package org.example.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("docker")
@ComponentScan(basePackages = {"org.example"})
@PropertySource("classpath:application-docker.properties")
public class DockerDispatcherConfiguration {
}
