package com.alshaya.microservice.com.alshaya.microservice.localization.domain;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationInfo {

    private TranslationId id;

    private String brand;

    private String language;

    private Map<String, Object> translation;

    public TranslationInfo(String brand, String language, Map<String, Object> translation) {
        this.id = new TranslationId(brand, language);
        this.translation = translation;
    }
}
