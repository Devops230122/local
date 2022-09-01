package com.alshaya.microservice.com.alshaya.microservice.localization.service.impl;

import com.alshaya.microservice.com.alshaya.microservice.localization.document.TranslationDocument;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.StoreContext;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationBatchUpdate;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationBatchUpdate.TranslationNode;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationInfo;
import com.alshaya.microservice.com.alshaya.microservice.localization.mapper.TranslationMapper;
import com.alshaya.microservice.com.alshaya.microservice.localization.repository.TranslationRepository;
import com.alshaya.microservice.com.alshaya.microservice.localization.service.TranslationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private static final String BRAND = "brand";
    private static final String LANGUAGE = "language";
    private static final String TRANSLATION_PREFIX = "translation.";
    private static final String DASH = "/";
    private static final String DOT = ".";

    private final TranslationRepository translationRepository;

    private final TranslationMapper translationMapper;

    private final ReactiveMongoTemplate mongoTemplate;

    private final CacheServiceImpl cacheService;

    @Override
    public Flux<TranslationInfo> getAllTranslations() {
        return cacheService.findAllCacheValues(
            translationRepository::findAll)
            .map(translationMapper::toDomainTranslation);
    }

    @Override
    public Mono<TranslationInfo> getTranslations(StoreContext store) {
        return cacheService.findCacheValueByBrandAndLanguage(
            store.brand(),
            store.language(),
            translationRepository::findTranslationEntityByBrandAndLanguage)
            .map(translationMapper::toDomainTranslation);
    }

    @Override
    public Flux<TranslationInfo> getTranslationByBrand(String brand) {
        return cacheService.findCacheValueByBrand(
            brand,
            translationRepository::findTranslationEntityByBrand)
            .map(translationMapper::toDomainTranslation);
    }

    @Override
    public Mono<TranslationInfo> addTranslation(TranslationInfo translation) {
        return translationRepository.save(
            new TranslationDocument(
                translation.getId().brand(),
                translation.getId().language(),
                new Document(translation.getTranslation())))
            .flatMap(cacheService::updateCacheValue)
            .map(translationMapper::toDomainTranslation);
    }

    @Override
    public Mono<TranslationInfo> updateTranslation(
        TranslationBatchUpdate translationBatchUpdate) {
        return mongoTemplate
            .findAndModify(
                buildQueryByBrandAndLanguage(translationBatchUpdate.getId().brand(),
                    translationBatchUpdate.getId().language()),
                buildUpdateQuery(translationBatchUpdate.getUpdates()),
                FindAndModifyOptions.options().returnNew(true),
                TranslationDocument.class)
            .flatMap(cacheService::updateCacheValue)
            .map(translationMapper::toDomainTranslation);
    }

    private Query buildQueryByBrandAndLanguage(String brand, String language) {
        Query query = new Query();

        query.addCriteria(Criteria.where(BRAND).is(brand).and(LANGUAGE).is(language));

        return query;
    }

    private Update buildUpdateQuery(List<TranslationNode> updates) {
        Update update = new Update();
        updates.forEach(node -> update.set(TRANSLATION_PREFIX +
            node.getKey().replace(DASH, DOT),
            node.getValue()));

        return update;
    }
}
