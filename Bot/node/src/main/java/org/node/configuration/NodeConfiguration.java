package org.node.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"org.node"})
@PropertySource("classpath:appp.properties")
public class NodeConfiguration {
}
