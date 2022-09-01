package com.alshaya.microservice.com.alshaya.microservice.localization.controller.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.TranslationDTO;
import com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.request.TranslationBatchUpdateRequestDTO;
import com.alshaya.microservice.com.alshaya.microservice.localization.document.TranslationDocument;
import com.alshaya.microservice.com.alshaya.microservice.localization.repository.TranslationRepository;
import com.alshaya.test.utils.FixtureFromResource;
import com.alshaya.test.utils.TestResource;
import com.azure.spring.cloud.autoconfigure.cosmos.AzureCosmosAutoConfiguration;
import com.azure.spring.cloud.autoconfigure.data.cosmos.CosmosDataAutoConfiguration;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.function.Predicate;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnableAutoConfiguration(
    exclude = {AzureCosmosAutoConfiguration.class, CosmosDataAutoConfiguration.class})
@TestResource(path = "fixtures/controller/translation-controller.yml")
class TranslationControllerIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer =
        new MongoDBContainer(DockerImageName.parse("mongo").withTag("4.0.10"));

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TranslationRepository translationRepository;

    @ParameterizedTest
    @FixtureFromResource
    void whenAddTranslationRequestReceived_thenTranslationSavedToDB(
        String brand,
        String language,
        String translationId,
        String newKeyValue,
        TranslationDTO translation,
        TranslationBatchUpdateRequestDTO batchUpdate) {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/translation/v1/brand/language/upsert")
                .build())
            .bodyValue(translation)
            .exchange().expectStatus()
            .isOk();

        findByBrandAndLanguageAndVerify(brand, language,
            (TranslationDocument tr) -> tr.getBrand().equals(brand));

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/translation/v1/brand/language/update")
                .build())
            .bodyValue(batchUpdate)
            .exchange().expectStatus()
            .isOk();

        findByBrandAndLanguageAndVerify(brand, language, (TranslationDocument tr) -> {
            JsonObject asJsonObject =
                JsonParser.parseString(tr.getTranslation().toJson()).getAsJsonObject();
            return newKeyValue.equals(asJsonObject.get("level_1").getAsJsonObject().get("level_2")
                .getAsJsonObject().get("key_3").getAsString());
        });
    }

    @ParameterizedTest
    @FixtureFromResource
    void whenBatchUpdateTranslationRequestReceived_thenTranslationSavedToDB(
        String brand,
        String language,
        String translationId,
        String newKeyValue,
        TranslationDTO translation,
        TranslationBatchUpdateRequestDTO batchUpdate) {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/translation/v1/brand/language/upsert")
                .build())
            .bodyValue(translation)
            .exchange().expectStatus()
            .isOk();

        findByBrandAndLanguageAndVerify(brand, language,
            (TranslationDocument tr) -> tr.getBrand().equals(brand));

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/translation/v1/brand/language/update")
                .build())
            .bodyValue(batchUpdate)
            .exchange().expectStatus()
            .isOk();

        findByBrandAndLanguageAndVerify(brand, language, (TranslationDocument tr) -> {
            JsonObject asJsonObject =
                JsonParser.parseString(tr.getTranslation().toJson()).getAsJsonObject();
            return newKeyValue.equals(asJsonObject.get("level_1").getAsJsonObject().get("level_2")
                .getAsJsonObject().get("key_3").getAsString())
                && newKeyValue.equals(
                    asJsonObject.get("level_1").getAsJsonObject().get("key_2").getAsString());
        });
    }

    @ParameterizedTest
    @FixtureFromResource
    void whenBatchUpdateTranslationForSameBrandAndDifferentLanguageRequestReceived_thenCacheCorrectlyUpdated(
        String brand,
        String language,
        TranslationDTO translation) {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/translation/v1/brand/language/upsert")
                .build())
            .bodyValue(translation)
            .exchange().expectStatus()
            .isOk();

        findByBrandAndLanguageAndVerify(brand, language,
            (TranslationDocument tr) -> tr.getBrand().equals(brand));

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/translation/v1/brand/language1/upsert")
                .build())
            .bodyValue(translation)
            .exchange().expectStatus()
            .isOk();

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/translation/v1/brand/list")
                .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("language1").isNotEmpty()
            .jsonPath("language").isNotEmpty();;
    }

    private void findByBrandAndLanguageAndVerify(
        String brand,
        String language,
        Predicate<TranslationDocument> predicate) {
        StepVerifier.create(
            translationRepository
                .findTranslationEntityByBrandAndLanguage(
                    brand, language))
            .assertNext(translationDoc -> assertTrue(predicate.test(translationDoc)))
            .verifyComplete();
    }
}
