package com.particle.asset.manager.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching // Attiva il sistema di caching di Spring nell'intera applicazione.
// Grazie a questa annotazione, "@Cacheable" e "@CacheEvict" possono funzionare.
public class CacheConfig
{
    // Durata della cache → va a prendere il valore all'interno del file properties
    // Dopo la durata (28800000ms = 8h), la cache scade e i dati devono essere ricaricati
    // dal database.
    @Value("${cache.ttl}")
    private long cacheTtl;

    // Metodo di creazione e configurazione del gestore della cache che spring userà
    // per le operazioni di caching.
    // All'interno, vengono create 3 cache indipendenti e separate con i nomi
    // "assetTypes", "businessUnits", "assetStatusTypes".
    @Bean
    public CacheManager cacheManager()
    {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "assetTypes",
                "businessUnits",
                "assetStatusTypes",
                "assets"
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheTtl, TimeUnit.MILLISECONDS)
                .maximumSize(1000)
                .recordStats());

        return cacheManager;
    }
}