package com.alshaya.microservice.com.alshaya.microservice.localization.document;

import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "translation")
public class TranslationDocument implements Serializable {
    public static final String TRANSLATION_ID_SCHEMA = "%s-%s";

    @Id
    private String id;

    private String brand;

    @PartitionKey
    private String language;

    private Document translation;

    public TranslationDocument(String brand, String language, Document translation) {
        this(String.format(TRANSLATION_ID_SCHEMA, brand, language), brand, language, translation);
    }
}
