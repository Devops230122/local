package com.alshaya.microservice.com.alshaya.microservice.localization.utils;

import com.alshaya.microservice.com.alshaya.microservice.localization.exception.ObjectMappingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectMappingUtil {
    private ObjectMappingUtil() {}

    public static <T> byte[] convertToByteArray(T data, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.error("Can not map object " + data.getClass(), e);
            throw new ObjectMappingException("Can not map object " + data.getClass(), e);
        }
    }
}
