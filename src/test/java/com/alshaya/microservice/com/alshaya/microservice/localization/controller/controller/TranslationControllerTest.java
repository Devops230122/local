package com.alshaya.microservice.com.alshaya.microservice.localization.controller.controller;

import com.alshaya.microservice.com.alshaya.microservice.localization.config.ObjectMapperConfig;
import com.alshaya.microservice.com.alshaya.microservice.localization.controller.TranslationController;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationInfo;
import com.alshaya.microservice.com.alshaya.microservice.localization.mapper.TranslationMapper;
import com.alshaya.microservice.com.alshaya.microservice.localization.service.TranslationService;
import com.alshaya.microservice.com.alshaya.microservice.localization.service.impl.TranslationServiceImpl;
import com.alshaya.test.utils.FixtureFromResource;
import com.alshaya.test.utils.TestResource;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@ActiveProfiles("integration-test")
@WebFluxTest({
    ObjectMapperConfig.class,
    TranslationController.class,
    TranslationService.class,
    TranslationMapper.class,
    WebTestClient.class
})
@TestResource(path = "fixtures/controller/translation-controller-mock.yml")
class TranslationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TranslationServiceImpl translationService;

    @ParameterizedTest
    @FixtureFromResource
    void whenGetBrandTranslationAsFile_thenReturnTranslationAsFile(
        String brand,
        TranslationInfo translation) {
        var serviceResponse = Flux.just(translation);

        Mockito.when(translationService.getTranslationByBrand(brand))
            .thenReturn(serviceResponse);

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/translation/v1/{brand}/list").build(brand))
            .header("Accept", MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .exchange()
            .expectHeader()
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .expectHeader()
            .contentDisposition(
                ContentDisposition.attachment().filename("translation.json").build())
            .expectBody().jsonPath("$").isNotEmpty();
    }

    @ParameterizedTest
    @FixtureFromResource
    void whenGetBrandTranslation_thenReturnTranslationAsJson(
        String brand,
        TranslationInfo translation) {
        var serviceResponse = Flux.just(translation);

        Mockito.when(translationService.getTranslationByBrand(brand))
            .thenReturn(serviceResponse);

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/translation/v1/{brand}/list").build(brand))
            .exchange()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .expectBody().jsonPath("$").isNotEmpty();
    }
}
