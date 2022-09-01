package com.alshaya.microservice.com.alshaya.microservice.localization.app_health_indication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.stereotype.Component;

@Slf4j
@Component("readiness")
public class AppReadinessIndicator extends ReadinessStateHealthIndicator {

    public AppReadinessIndicator(ApplicationAvailability availability) {
        super(availability);
    }
}
