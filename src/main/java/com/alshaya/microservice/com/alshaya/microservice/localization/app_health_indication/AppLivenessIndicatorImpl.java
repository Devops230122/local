package com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication;

import com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication.indicators.AppVerifcationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.availability.LivenessState;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component("liveness")
public class AppLivenessIndicatorImpl implements ReactiveHealthIndicator {

    @Autowired
    private AppVerifcationService databaseConnectivityVerificationService;

    @Override
    public Mono<Health> health() {
        return checkLivenessState()
            .map(this::buildHealthIndicator);
    }

    private Mono<LivenessState> checkLivenessState() {
        return databaseConnectivityVerificationService
            .check()
            .map(this::defineLivenessState);
    }

    private LivenessState defineLivenessState(final boolean result) {
        return result
            ? LivenessState.CORRECT
            : LivenessState.BROKEN;
    }

    private Health buildHealthIndicator(final LivenessState livenessState) {
        return LivenessState.CORRECT.equals(livenessState)
            ? new Health.Builder().up().build()
            : new Health.Builder().down().build();
    }
}
