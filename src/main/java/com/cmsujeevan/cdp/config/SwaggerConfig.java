package com.cmsujeevan.cdp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openApiConfiguration() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info().title("CDP Bulk API").description("")
                .version("1.0").contact(new Contact().name("Sujeevan Mahendran").url("").email("sujeevan.mahendran@sysco.com"));
    }
}

