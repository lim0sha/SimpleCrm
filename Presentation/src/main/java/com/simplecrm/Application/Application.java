package com.simplecrm.Application;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@OpenAPIDefinition(
        info = @Info(
                title = "SimpleCRM API",
                version = "1.0",
                description = "API for sellers and transactions management powered by Spring Boot"
        )
)
@SpringBootApplication
@ComponentScan({"com.simplecrm.Application",
        "com.simplecrm.Models.Entities",
        "com.simplecrm.Configs",
        "com.simplecrm.Services",
        "com.simplecrm.Repositories",
        "com.simplecrm.Controllers",
        "com.simplecrm.Handlers",
        "com.simplecrm.Utils"})
public class Application {
    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}