package org.buspark;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.buspark.ui.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
@EnableScheduling
public class MainApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(MainApplication.class);

        boolean isHeadless = GraphicsEnvironment.isHeadless();

        if (isHeadless) {
            System.out.println("--- RUNNING IN CLOUD (HEADLESS) ---");
            builder.headless(true).run(args);
        } else {
            System.out.println("--- RUNNING LOCALLY (GUI ENABLED) ---");
            ConfigurableApplicationContext context = builder.headless(false).run(args);

            EventQueue.invokeLater(() -> {
                FlatMacLightLaf.setup();
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });
        }
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("https://*.vercel.app", "http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}