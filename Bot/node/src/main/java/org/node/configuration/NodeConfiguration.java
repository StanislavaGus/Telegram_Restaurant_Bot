package org.node.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@Profile("node")
@ComponentScan(basePackages = {"org.node"})
@EnableR2dbcRepositories(basePackages = "org.node.repository")
public class NodeConfiguration {
}
