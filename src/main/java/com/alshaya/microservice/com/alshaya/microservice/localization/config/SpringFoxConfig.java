package com.alshaya.microservice.com.alshaya.microservice.localization.config;

import static java.util.Collections.singletonList;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

    private static final String API_VERSION_1 = "v1";

    private ApiInfo apiInfo(String basePath) {
        return new ApiInfoBuilder().title("API Documentation").description(basePath).build();
    }

    @Bean
    public Docket apiV1(@Value("${spring.webflux.base-path}") String basePath) {
        return new Docket(DocumentationType.OAS_30).pathMapping(basePath)
            .ignoredParameterTypes(ServerHttpRequest.class)
            .groupName(API_VERSION_1)
            .securitySchemes(
                List.of(new ApiKey(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION, "header")))
            .securityContexts(List.of(securityContext()))
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.regex("/translation/" + API_VERSION_1 + ".*"))
            .build()
            .apiInfo(apiInfo(basePath));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(securityReferences()).build();
    }

    private List<SecurityReference> securityReferences() {
        return singletonList(new SecurityReference(
            HttpHeaders.AUTHORIZATION,
            new AuthorizationScope[] {new AuthorizationScope("global", "global")}));
    }
}
