package com.simplecrm.Configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EnableJpaRepositories(basePackages = "com.simplecrm.Repositories")
@EntityScan(basePackages = "com.simplecrm.Models.Entities")
public class JPAConfig {
}