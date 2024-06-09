package org.node.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("node")
@ComponentScan(basePackages = {"org.node"})
public class NodeConfiguration {
}

