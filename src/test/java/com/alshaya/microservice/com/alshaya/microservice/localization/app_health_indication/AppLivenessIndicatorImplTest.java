package com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication.indicators.impl.database.DatabaseConnectivityVerificationService;
import com.alshaya.test.utils.FixtureFromResource;
import com.alshaya.test.utils.TestResource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@TestResource(path = "/fixtures/app_health_indication/app-liveness-indicator-impl.yml")
@ExtendWith(MockitoExtension.class)
class AppLivenessIndicatorImplTest {

    @Mock
    private DatabaseConnectivityVerificationService databaseConnectivityVerificationService;

    @InjectMocks
    private AppLivenessIndicatorImpl appLivenessIndicator;

    @ParameterizedTest
    @FixtureFromResource
    void health_allChecksPassed_statusIsUp(final Boolean resultOfDataConnectionCheck) {
        final var expectedResult = new Health.Builder().up().build();

        when(databaseConnectivityVerificationService
            .check())
                .thenReturn(Mono.just(resultOfDataConnectionCheck));

        StepVerifier
            .create(appLivenessIndicator.health())
            .assertNext(actualResult -> assertEquals(expectedResult.getStatus().getCode(),
                actualResult.getStatus().getCode()))
            .verifyComplete();
    }

    @ParameterizedTest
    @FixtureFromResource
    void health_allChecksFailed_statusIsDown(final Boolean resultOfDataConnectionCheck) {
        final var expectedResult = new Health.Builder().down().build();

        when(databaseConnectivityVerificationService
            .check())
                .thenReturn(Mono.just(resultOfDataConnectionCheck));

        StepVerifier
            .create(appLivenessIndicator.health())
            .assertNext(actualResult -> assertEquals(expectedResult.getStatus().getCode(),
                actualResult.getStatus().getCode()))
            .verifyComplete();
    }
}
