package com.example.demo.config;

import org.apache.catalina.Context;
import org.apache.catalina.session.StandardManager;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addContextCustomizers((Context context) -> {
            // Disable Tomcat session persistence to avoid deserialization issues
            StandardManager manager = new StandardManager();
            // empty pathname disables saving sessions to disk
            manager.setPathname("");
            context.setManager(manager);
        });
    }
}


