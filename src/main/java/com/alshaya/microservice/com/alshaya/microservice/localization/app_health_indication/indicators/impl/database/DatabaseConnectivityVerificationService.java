package com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication.indicators.impl.database;

import com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication.indicators.AppVerifcationService;
import com.alshaya.microservice.com.alshaya.microservice.localization.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseConnectivityVerificationService
    implements AppVerifcationService {

    private final TranslationRepository translationRepository;

    @Override
    public Mono<Boolean> check() {
        //todo check for connectivity check without explicit paid call to Cosmo
        return Mono.just(Boolean.TRUE);
    }
}
