package org.node.configuration;

import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:appp.properties")
public class TomcatConfigg {

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
