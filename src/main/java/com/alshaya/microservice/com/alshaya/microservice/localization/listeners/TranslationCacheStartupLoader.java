package com.alshaya.microservice.com.alshaya.microservice.localization.listeners;

import com.alshaya.microservice.com.alshaya.microservice.localization.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@ConditionalOnProperty(name = "cache.pre-loading.enabled", havingValue = "true")
@RequiredArgsConstructor
public class TranslationCacheStartupLoader {

    private final TranslationService translationService;

    @EventListener(ApplicationReadyEvent.class)
    public void loadDeliveryAddressLocationsIntoCache() {
        log.info("Pre-loading of translations - has started.");
        translationService
            .getAllTranslations()
            .subscribeOn(Schedulers.parallel())
            .subscribe();
    }
}
