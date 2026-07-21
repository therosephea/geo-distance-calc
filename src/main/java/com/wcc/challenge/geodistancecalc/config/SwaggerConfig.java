package com.wcc.challenge.geodistancecalc.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Geo Distance Calculator")
                .version("v1")
                .description("Straight-line distance between two UK postal codes."));
    }
}
