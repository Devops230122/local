package com.alshaya.microservice.com.alshaya.microservice.localization.repository;

import com.alshaya.microservice.com.alshaya.microservice.localization.document.TranslationDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TranslationRepository
    extends ReactiveMongoRepository<TranslationDocument, String> {

    Mono<TranslationDocument> findById(String id);

    Flux<TranslationDocument> findAll();

    Mono<TranslationDocument> findTranslationEntityByBrandAndLanguage(String brand,
        String language);

    Flux<TranslationDocument> findTranslationEntityByBrand(String brand);
}
