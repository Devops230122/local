package com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("language")
    private String language;
    @JsonProperty("translation")
    private Map<String, Object> translation;
}
