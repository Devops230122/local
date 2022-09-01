package com.alshaya.microservice.com.alshaya.microservice.localization.aspect;

import static com.alshaya.microservice.com.alshaya.microservice.localization.config.property.LoggingConstants.CLASS_PREFIX;
import static com.alshaya.microservice.com.alshaya.microservice.localization.config.property.LoggingConstants.COLON;
import static com.alshaya.microservice.com.alshaya.microservice.localization.config.property.LoggingConstants.LOG_SUFFIX;
import static com.alshaya.microservice.com.alshaya.microservice.localization.config.property.LoggingConstants.METHOD_INPUT_ARGS_PREFIX;
import static com.alshaya.microservice.com.alshaya.microservice.localization.config.property.LoggingConstants.METHOD_PREFIX;
import static com.alshaya.microservice.com.alshaya.microservice.localization.utils.CollectionUtils.zipToMap;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Aspect
@Slf4j
@Component
@ConditionalOnProperty(name = "microservice.endpoints.default.logging.enabled",
    havingValue = "true")
public class LoggingAspect {

    @Value("${microservice.endpoints.default.logging.include-method-arg:false}")
    private boolean includeMethodArgs;

    @Around("execution(* com.alshaya.microservice.com.alshaya.microservice.localization.controller.*.*(..))")
    public Object logSignatureArguments(final ProceedingJoinPoint proceedingJoinPoint) {
        final var signature = (MethodSignature) proceedingJoinPoint.getSignature();
        final var args = proceedingJoinPoint.getArgs();

        if (signature.getReturnType().equals(Mono.class)) {
            return monoResult(proceedingJoinPoint, args);
        } else
            if (signature.getReturnType().equals(Flux.class)) {
                return fluxResult(proceedingJoinPoint, args);
            } else {
                throw new UnsupportedOperationException(
                    "Return class is not supported: " + signature.getReturnType());
            }
    }

    @SuppressWarnings("PMD")
    private Flux<?> fluxResult(final ProceedingJoinPoint proceedingJoinPoint, final Object[] args) {
        try {
            performLogging(proceedingJoinPoint, args);
            return (Flux<?>) proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception caught within aspect path variables substitution: " + e);
            return Flux.error(e);
        }
    }

    @SuppressWarnings("PMD")
    private Mono<?> monoResult(final ProceedingJoinPoint proceedingJoinPoint, final Object[] args) {
        try {
            performLogging(proceedingJoinPoint, args);
            return (Mono<?>) proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception caught within aspect path variables substitution: " + e);
            return Mono.error(e);
        }
    }

    private void performLogging(final ProceedingJoinPoint proceedingJoinPoint,
        final Object... args) {
        final var resultLogBuilder = new StringBuilder();
        final var signature = (MethodSignature) proceedingJoinPoint.getSignature();
        final var paramNames = signature.getParameterNames();

        appendValueWithPrefix(CLASS_PREFIX, resultLogBuilder,
            signature.getDeclaringType().getSimpleName());
        appendValueWithPrefix(METHOD_PREFIX, resultLogBuilder, signature.getName());

        if (includeMethodArgs && !ObjectUtils.isEmpty(args)) {
            resultLogBuilder.append(METHOD_INPUT_ARGS_PREFIX);
            zipToMap(Arrays.asList(paramNames), Arrays.asList(args)).forEach(
                (key, value) -> appendValueWithPrefix(key, resultLogBuilder,
                    String.valueOf(value)));
        }

        log.info(resultLogBuilder.toString());
    }

    private void appendValueWithPrefix(final String prefix, final StringBuilder resultLogBuilder,
        final String value) {
        resultLogBuilder.append(StringUtils.capitalize(String.valueOf(prefix))).append(COLON)
            .append(value).append(LOG_SUFFIX);
    }
}
