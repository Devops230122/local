package com.alshaya.microservice.com.alshaya.microservice.localization.controller;

import com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.TranslationDTO;
import com.alshaya.microservice.com.alshaya.microservice.localization.controller.dto.request.TranslationBatchUpdateRequestDTO;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.StoreContext;
import com.alshaya.microservice.com.alshaya.microservice.localization.domain.TranslationInfo;
import com.alshaya.microservice.com.alshaya.microservice.localization.mapper.TranslationMapper;
import com.alshaya.microservice.com.alshaya.microservice.localization.service.TranslationService;
import com.alshaya.microservice.com.alshaya.microservice.localization.utils.ObjectMappingUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/translation/v1")
public class TranslationController {

    private static final String TRANSLATION_FILE_NAME = "translation.json";

    private final TranslationService translationService;
    private final TranslationMapper translationMapper;
    private final ObjectMapper objectMapper;

    @ApiResponse(responseCode = "200",
        description = "Get translation by language and brand")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiOperation(value = "Returns found translation",
        response = TranslationDTO.class)
    @GetMapping("/{brand}/{language}/list")
    public Mono<Object> getTranslation(
        @PathVariable String brand,
        @PathVariable String language) {
        return translationService
            .getTranslations(new StoreContext(brand, language))
            .map(translation -> Map.of(translation.getLanguage(),
                translationMapper.toTranslationDTO(translation)));
    }

    @ApiResponse(responseCode = "200",
        description = "Get translations by brand")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiOperation(value = "Returns found translations",
        response = TranslationDTO.class)
    @GetMapping("/{brand}/list")
    public Mono<ResponseEntity<byte[]>> getTranslationByBrand(
        @PathVariable String brand,
        @RequestHeader(value = "Accept", required = false) MediaType acceptHeader) {
        return translationService.getTranslationByBrand(brand)
            .collectMap(TranslationInfo::getLanguage, translationMapper::toTranslationDTO)
            .map(response -> new ResponseEntity<>(
                ObjectMappingUtil.convertToByteArray(response, objectMapper),
                configHeaders(acceptHeader), HttpStatus.OK));
    }

    @ApiResponse(responseCode = "200",
        description = "Upsert translation")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiOperation(value = "Returns created/updated translation",
        response = TranslationDTO.class)
    @PutMapping("/{brand}/{language}/upsert")
    public Mono<TranslationDTO> saveTranslation(
        @PathVariable String brand,
        @PathVariable String language,
        @Validated @RequestBody TranslationDTO translationDTO) {
        return translationService
            .addTranslation(new TranslationInfo(brand, language, translationDTO.getTranslation()))
            .map(translationMapper::toTranslationDTO);
    }

    @ApiResponse(responseCode = "200",
        description = "Update translation")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiOperation(value = "Returns updated translation",
        response = TranslationDTO.class)
    @PutMapping("/{brand}/{language}/update")
    public Mono<Object> updateTranslation(
        @PathVariable String brand,
        @PathVariable String language,
        @Validated @RequestBody TranslationBatchUpdateRequestDTO translationBatchUpdateDTO) {
        return translationService.updateTranslation(translationMapper
            .mapToTranslationBatchUpdate(brand, language, translationBatchUpdateDTO))
            .map(translation -> Map.of(translation.getLanguage(),
                translationMapper.toTranslationDTO(translation)));
    }

    private HttpHeaders configHeaders(MediaType acceptHeader) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (MediaType.APPLICATION_OCTET_STREAM.equals(acceptHeader)) {
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentDisposition(
                ContentDisposition.attachment().filename(TRANSLATION_FILE_NAME).build());
        }
        return httpHeaders;
    }
}
