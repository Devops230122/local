package com.alshaya.microservice.com.alshaya.microservice.localization.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HazelcastCacheConfiguration {

    public static final String CACHE_TRANSLATIONS = "translations-cache";

    @Value("${cache.instance.name}")
    private String cacheInstanceName;

    @Value("${cache.time-to-live-seconds}")
    private Integer timeToLiveSeconds;

    @Getter
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public void init() {
        this.hazelcastInstance = Hazelcast.newHazelcastInstance();

        final Config config = this.hazelcastInstance.getConfig();
        config.setInstanceName(cacheInstanceName);

        config.addMapConfig(initTranslationsCache());
    }

    private MapConfig initTranslationsCache() {
        final MapConfig locationsCache = new MapConfig();
        locationsCache.setName(CACHE_TRANSLATIONS);
        locationsCache.setTimeToLiveSeconds(timeToLiveSeconds);
        locationsCache.setEvictionPolicy(EvictionPolicy.LFU);

        return locationsCache;
    }
}
