package com.alshaya.microservice.com.alshaya.microservice.localization.service;

import com.alshaya.microservice.com.alshaya.microservice.localization.document.TranslationDocument;
import com.hazelcast.core.IMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CacheService {

    Mono<TranslationDocument> findCacheValueByBrandAndLanguage(
        String brand,
        String language,
        BiFunction<String, String, Mono<TranslationDocument>> retrievalMethod);

    Flux<TranslationDocument> findCacheValueByBrand(
        String brand,
        Function<String, Flux<TranslationDocument>> retrievalMethod);

    Flux<TranslationDocument> findAllCacheValues(
        Supplier<Flux<TranslationDocument>> retrievalMethod);

    Mono<TranslationDocument>
        updateCacheValue(TranslationDocument translation);

    IMap<String, Map<String, TranslationDocument>> getCache();
}
