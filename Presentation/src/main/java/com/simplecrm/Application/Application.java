package com.simplecrm.Application;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@OpenAPIDefinition(
        info = @Info(
                title = "SimpleCRM API",
                version = "1.0",
                description = "API for sellers and transactions management powered by Spring Boot"
        )
)
@ComponentScan({"com.simplecrm.Application",
        "com.simplecrm.Models.Entities",
        "com.simplecrm.Configs",
        "com.simplecrm.Services",
        "com.simplecrm.Repositories",
        "com.simplecrm.Controllers",
        "com.simplecrm.Handlers",
        "com.simplecrm.Utils"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Configuration
    static class DataSourceConfig {

        @Bean
        @Primary
        public DataSource dataSource() {
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            if (url == null || user == null || password == null) {
                throw new RuntimeException("DB_URL, DB_USER, DB_PASSWORD must be set in environment variables");
            }

            DataSourceProperties props = new DataSourceProperties();
            props.setUrl(url);
            props.setUsername(user);
            props.setPassword(password);
            props.setDriverClassName("org.postgresql.Driver");
            return props.initializeDataSourceBuilder().build();
        }
    }
}