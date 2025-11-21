package com.crosspay.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main Spring Boot Application for CrossPay Blockchain
 */
@SpringBootApplication
public class CrossPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrossPayApplication.class, args);
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   CrossPay Blockchain Server Started!         ║");
        System.out.println("║   API: http://localhost:8080/api              ║");
        System.out.println("║   Health: http://localhost:8080/actuator/health ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }

    /**
     * Configure CORS for frontend access
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "http://localhost:80",
                            "http://localhost:8080",
                            "http://frontend:80"  // Docker container name
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
