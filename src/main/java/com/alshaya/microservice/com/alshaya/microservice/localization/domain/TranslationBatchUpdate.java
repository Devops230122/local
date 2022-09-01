package com.alshaya.microservice.com.alshaya.microservice.localization.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationBatchUpdate {

    private TranslationId id;

    private List<TranslationNode> updates;

    public TranslationBatchUpdate(String brand, String language,
        List<TranslationNode> updates) {
        this.id = new TranslationId(brand, language);
        this.updates = updates;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TranslationNode {
        private String key;

        private String value;
    }
}
