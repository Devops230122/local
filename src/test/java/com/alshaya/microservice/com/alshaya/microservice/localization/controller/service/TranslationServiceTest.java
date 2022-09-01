package com.alshaya.microservice.com.alshaya.microservice.localization.controller.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alshaya.microservice.com.alshaya.microservice.localization.document.TranslationDocument;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationInfo;
import com.alshaya.microservice.com.alshaya.microservice.localization.mapper.TranslationMapper;
import com.alshaya.microservice.com.alshaya.microservice.localization.repository.TranslationRepository;
import com.alshaya.microservice.com.alshaya.microservice.localization.service.impl.CacheServiceImpl;
import com.alshaya.microservice.com.alshaya.microservice.localization.service.impl.TranslationServiceImpl;
import com.alshaya.test.utils.FixtureFromResource;
import com.alshaya.test.utils.TestResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.bson.Document;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockitoExtension.class})
@TestResource(path = "fixtures/service/translation-service.yml")
class TranslationServiceTest {

    @Mock
    private CacheServiceImpl cacheService;

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private TranslationMapper translationMapper;

    @InjectMocks
    private TranslationServiceImpl translationService;

    @Captor
    ArgumentCaptor<TranslationDocument> translationDocumentCaptor;

    @FixtureFromResource
    @ParameterizedTest
    void whenSaveTranslationCalled_thenTranslationSavedToDBAndCache(
        TranslationInfo translation,
        TranslationDocument translationDocument,
        Map<String, Object> translationString) {
        translation.setTranslation(translationString);
        translationDocument.setTranslation(
            mapValueToDocument(translationString));

        when(cacheService.updateCacheValue(any())).thenReturn(Mono.just(new TranslationDocument()));

        when(translationMapper.toDomainTranslation(any(TranslationDocument.class)))
            .thenReturn(new TranslationInfo());

        when(translationRepository.save(any(TranslationDocument.class)))
            .thenReturn(Mono.just(translationDocument));

        StepVerifier.create(translationService.addTranslation(
            translation))
            .assertNext(translationDTO -> {
                verify(
                    cacheService).updateCacheValue(translationDocumentCaptor.capture());
                TranslationDocument translationDocumentCreated =
                    translationDocumentCaptor.getValue();

                assertThat(translationDocumentCreated.getBrand(),
                    is(translationDocument.getBrand()));

                assertThat(translationDocumentCreated.getLanguage(),
                    is(translationDocument.getLanguage()));

                assertThat(
                    documentToMapValue(translationDocumentCreated),
                    is(translation.getTranslation()));

            }).verifyComplete();
    }

    @SneakyThrows
    private Document mapValueToDocument(
        Map<String, Object> jsonMap) {
        return new Document(jsonMap);
    }

    @SneakyThrows
    private Map<String, Object> documentToMapValue(TranslationDocument translationDocument) {
        return new ObjectMapper().readValue(translationDocument
            .getTranslation().toJson(), HashMap.class);
    }
}
