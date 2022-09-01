package com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication.indicators.impl.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.alshaya.microservice.com.alshaya.microservice.localization.repository.TranslationRepository;
import com.alshaya.test.utils.FixtureFromResource;
import com.alshaya.test.utils.TestResource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockitoSettings(strictness = Strictness.LENIENT)
@TestResource(
    path = "/fixtures/app_health_indication/indicators/impl/database/database-on-app-startup-verification-service-impl.yml")
@ExtendWith(MockitoExtension.class)
class DatabaseConnectivityVerificationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @InjectMocks
    private DatabaseConnectivityVerificationService databaseConnectivityVerificationService;

    @ParameterizedTest
    @FixtureFromResource
    void verifyWhetherRetrievalRequestsWorkCorrectly_allRequestsExecutedCorrectly_returnTrue(
        Long mongoResult,
        final Boolean expectedResult) {
        when(translationRepository.count()).thenReturn(Mono.just(mongoResult));

        StepVerifier
            .create(databaseConnectivityVerificationService
                .check())
            .assertNext(actualResult -> assertEquals(expectedResult, actualResult))
            .verifyComplete();
    }

    @ParameterizedTest
    @FixtureFromResource
    void verifyWhetherRetrievalRequestsWorkCorrectly_allRequestsExecutedCorrectly_returnFalse(
        final Boolean expectedResult) {
        when(translationRepository.count()).thenReturn(Mono.error(new RuntimeException()));

        StepVerifier
            .create(databaseConnectivityVerificationService
                .check())
            .assertNext(actualResult -> assertEquals(Boolean.TRUE, actualResult))
            .verifyComplete();
    }
}
