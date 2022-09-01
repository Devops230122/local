package com.alshaya.microservice.com.alshaya.microservice.localization.domain;

public record TranslationId(String id, String brand, String language) {

    public static final String TRANSLATION_ID_SCHEMA = "%s-%s";

    public TranslationId(String brand, String language) {
        this(String.format(TRANSLATION_ID_SCHEMA, brand, language), brand, language);
    }
}
