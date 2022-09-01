package com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationBatchUpdateRequestDTO {

    @NotNull
    @JsonProperty("updates")
    private List<TranslationNodeDTO> updates;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TranslationNodeDTO {
        @NotBlank
        @JsonProperty("key")
        private String key;
        @NotBlank
        @JsonProperty("value")
        private String value;
    }
}
