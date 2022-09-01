package com.alshaya.microservice.com.alshaya.microservice.localization.service;

import com.alshaya.microservice.com.alshaya.microservice.localization.domain.StoreContext;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationBatchUpdate;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TranslationService {

    Flux<TranslationInfo> getAllTranslations();

    Mono<TranslationInfo> getTranslations(StoreContext store);

    Flux<TranslationInfo> getTranslationByBrand(String brand);

    Mono<TranslationInfo> addTranslation(TranslationInfo translation);

    Mono<TranslationInfo> updateTranslation(
        TranslationBatchUpdate translationBatchUpdate);
}
