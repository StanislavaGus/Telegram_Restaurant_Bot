package org.node.configuration;

import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"org.node"})
@PropertySource("classpath:app.properties")
public class TomcatConfig1 {

    @Value("${server.port}")
    private int serverPort;

    @Bean
    public Tomcat tomcat() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(serverPort);
        tomcat.getConnector(); // Это необходимо для инициализации Tomcat
        tomcat.start();
        return tomcat;
    }
}
