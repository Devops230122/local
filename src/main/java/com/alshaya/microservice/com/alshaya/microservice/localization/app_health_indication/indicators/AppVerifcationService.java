package com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication.indicators;

import reactor.core.publisher.Mono;

public interface AppVerifcationService {

    Mono<Boolean> check();
}
