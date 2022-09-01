package com.alshaya.microservice.com.alshaya.microservice.localization.mapper;

import com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.TranslationDTO;
import com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.request.TranslationBatchUpdateRequestDTO;
import com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.request.TranslationBatchUpdateRequestDTO.TranslationNodeDTO;
import com.alshaya.microservice.com.alshaya.microservice.localization.document.TranslationDocument;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationBatchUpdate;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationBatchUpdate.TranslationNode;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationId;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.bson.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TranslationMapper {

    @Mapping(target = "id", source = "translation.id.id")
    @Mapping(target = "brand", source = "translation.id.brand")
    @Mapping(target = "language", source = "translation.id.language")
    TranslationDTO
        toTranslationDTO(TranslationInfo translation);

    @Mapping(target = "translation", expression = "java(toTranslation(translationDocument))")
    @Mapping(target = "id", expression = "java(toTranslationId(translationDocument))")
    TranslationInfo
        toDomainTranslation(TranslationDocument translationDocument);

    TranslationNode
        toTranslationNode(TranslationNodeDTO translationNode);

    @Mapping(target = "id", expression = "java(toTranslationId(brand, language))")
    @Mapping(target = "updates",
        expression = "java(toTranslationNodeList(translationBatchUpdateRequestDTO))")
    TranslationBatchUpdate mapToTranslationBatchUpdate(String brand, String language,
        TranslationBatchUpdateRequestDTO translationBatchUpdateRequestDTO);

    default Map<String, Object> toTranslation(TranslationDocument translationDocument) {
        return Optional.ofNullable(translationDocument.getTranslation())
            .map(Document::toJson)
            .map(this::jsonStringToMap)
            .orElse(new HashMap<>());
    }

    default TranslationId toTranslationId(String brand, String language) {
        return new TranslationId(brand, language);
    }

    default TranslationId toTranslationId(TranslationDocument translationDocument) {
        return new TranslationId(translationDocument.getBrand(), translationDocument.getLanguage());
    }

    default List<TranslationNode>
        toTranslationNodeList(TranslationBatchUpdateRequestDTO requestDTO) {
        return requestDTO.getUpdates()
            .stream()
            .map(this::toTranslationNode).toList();
    }

    @SneakyThrows
    private Map<String, Object> jsonStringToMap(String jsonString) {
        return new ObjectMapper().readValue(jsonString, HashMap.class);
    }
}
