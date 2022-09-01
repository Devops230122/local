package com.alshaya.microservice.com.alshaya.microservice.localization.service.impl;

import com.alshaya.microservice.com.alshaya.microservice.localization.config.HazelcastCacheConfiguration;
import com.alshaya.microservice.com.alshaya.microservice.localization.document.TranslationDocument;
import com.alshaya.microservice.com.alshaya.microservice.localization.service.CacheService;
import com.hazelcast.core.IMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.cache.CacheFlux;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

@RequiredArgsConstructor
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final HazelcastCacheConfiguration cacheConfiguration;

    @Override
    public Mono<TranslationDocument> findCacheValueByBrandAndLanguage(
        String brand,
        String language,
        BiFunction<String, String, Mono<TranslationDocument>> retrievalMethod) {
        final IMap<String, Map<String, TranslationDocument>> translationsCache =
            cacheConfiguration.getHazelcastInstance()
                .getMap(HazelcastCacheConfiguration.CACHE_TRANSLATIONS);

        return CacheMono
            .lookup(key -> Mono.justOrEmpty(translationsCache.get(brand))
                .map(brandTranslations -> brandTranslations.get(language))
                .map(Signal::next),
                brand)
            .onCacheMissResume(() -> callDatabaseIfCacheEmpty(brand, language, retrievalMethod))
            .andWriteWith(
                (key, signal) -> Mono.fromRunnable(
                    () -> updateBrandLanguageValueInCacheEntry(translationsCache, signal.get())));
    }

    @Override
    public Flux<TranslationDocument> findCacheValueByBrand(
        String brand,
        Function<String, Flux<TranslationDocument>> retrievalMethod) {
        final IMap<String, Map<String, TranslationDocument>> translationsCache =
            cacheConfiguration.getHazelcastInstance()
                .getMap(HazelcastCacheConfiguration.CACHE_TRANSLATIONS);

        return CacheFlux
            .lookup(
                key -> Objects.isNull(translationsCache.get(brand)) ? Mono.empty()
                    : Flux.fromIterable(translationsCache.get(brand).values())
                        .map(Signal::next)
                        .collectList(),
                brand)
            .onCacheMissResume(() -> callDatabaseIfCacheEmpty(brand, retrievalMethod))
            .andWriteWith((key, signals) -> Mono.fromRunnable(() -> signals.forEach(
                signal -> updateBrandLanguageValueInCacheEntry(translationsCache, signal.get()))));
    }

    @Override
    public Flux<TranslationDocument> findAllCacheValues(
        Supplier<Flux<TranslationDocument>> retrievalMethod) {
        final IMap<String, Map<String, TranslationDocument>> translationsCache = getCache();

        return CacheFlux
            .lookup(
                key -> translationsCache.values().isEmpty() ? Mono.empty()
                    : Flux.fromIterable(translationsCache.values().stream()
                        .flatMap(valuesOfBrands -> valuesOfBrands.values().stream()).toList())
                        .map(Signal::next)
                        .collectList(),
                HazelcastCacheConfiguration.CACHE_TRANSLATIONS)
            .onCacheMissResume(() -> callDatabaseIfCacheEmpty(retrievalMethod))
            .andWriteWith((key, signals) -> Mono.fromRunnable(() -> signals.forEach(
                signal -> updateBrandLanguageValueInCacheEntry(translationsCache, signal.get()))));
    }

    @Override
    public Mono<TranslationDocument>
        updateCacheValue(TranslationDocument translation) {
        final IMap<String, Map<String, TranslationDocument>> translationsCache =
            cacheConfiguration.getHazelcastInstance()
                .getMap(HazelcastCacheConfiguration.CACHE_TRANSLATIONS);

        updateBrandLanguageValueInCacheEntry(translationsCache, translation);
        return Mono.just(translation);
    }

    @Override
    public IMap<String, Map<String, TranslationDocument>> getCache() {
        return cacheConfiguration.getHazelcastInstance()
            .getMap(HazelcastCacheConfiguration.CACHE_TRANSLATIONS);
    }

    private void updateBrandLanguageValueInCacheEntry(
        IMap<String, Map<String, TranslationDocument>> translationsCache,
        TranslationDocument translation) {
        if (Objects.isNull(translation)) {
            return;
        }
        Optional.ofNullable(translationsCache
            .get(translation.getBrand()))
            .ifPresentOrElse(
                brandValues -> {
                    brandValues.put(translation.getLanguage(), translation);
                    translationsCache.set(translation.getBrand(), brandValues);
                },
                () -> translationsCache.put(translation.getBrand(),
                    new HashMap<>(Map.of(translation.getLanguage(), translation))));
    }

    private Mono<TranslationDocument> callDatabaseIfCacheEmpty(String brand,
        String language,
        BiFunction<String, String, Mono<TranslationDocument>> retrievalMethod) {
        return retrievalMethod.apply(brand, language)
            .doOnSubscribe(
                subscription -> log.info("Call to retrieve values by brand and language executed"));
    }

    private Flux<TranslationDocument> callDatabaseIfCacheEmpty(String brand,
        Function<String, Flux<TranslationDocument>> retrievalMethod) {
        return retrievalMethod.apply(brand)
            .doOnSubscribe(subscription -> log.info("Call to retrieve values by brand executed"));
    }

    private Flux<TranslationDocument> callDatabaseIfCacheEmpty(
        Supplier<Flux<TranslationDocument>> retrievalMethod) {
        return retrievalMethod.get()
            .doOnSubscribe(ignored -> log
                .info("Call to retrieve all values by brands and languages executed"));
    }
}
