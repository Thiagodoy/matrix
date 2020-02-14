package com.core.matrix.configuration;

import com.google.common.base.Predicates;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build()
                .securitySchemes(Arrays.asList(apiKey()))
                .apiInfo(apiInfo());
    }

    private ApiKey apiKey() {
        return new ApiKey("authkey", "Authorization", "header");
    }

    private ApiInfo apiInfo() {

        return new ApiInfo(
                "API Matrix",
                "Core",
                "v1.0.0",
                "Terms of service",
                new Contact("Thiago H. Godoy", "", ""),
                "License of API", "API license URL", Collections.emptyList());
    }

}
