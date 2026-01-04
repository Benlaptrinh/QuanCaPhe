package com.example.demo.config;

import org.apache.catalina.Context;
import org.apache.catalina.session.StandardManager;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Disable Tomcat session persistence to avoid deserialization errors between restarts.
 * This sets the StandardManager pathname to empty which prevents writing sessions to disk.
 */
@Configuration
public class TomcatConfig {

    @Bean
    public TomcatContextCustomizer tomcatContextCustomizer() {
        return new TomcatContextCustomizer() {
            @Override
            public void customize(Context context) {
                try {
                    StandardManager manager = new StandardManager();
                    // empty pathname disables session persistence to disk
                    manager.setPathname("");
                    context.setManager(manager);
                } catch (Exception ex) {
                    // if anything goes wrong, just log to stdout (avoid adding extra logging dependency)
                    System.out.println("Failed to customize Tomcat context for session persistence: " + ex.getMessage());
                }
            }
        };
    }
}


