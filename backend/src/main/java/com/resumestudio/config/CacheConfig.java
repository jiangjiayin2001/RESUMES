package com.resumestudio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.resumestudio.common.CacheNames;
import com.resumestudio.config.properties.AppProperties;
import com.resumestudio.resume.dto.ResumeResponse;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(AppProperties appProperties) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Jackson2JsonRedisSerializer<ResumeResponse> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, ResumeResponse.class);

        RedisCacheConfiguration resumeCache = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(appProperties.getCache().getResumeTtl())
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                );

        return builder -> builder
                .withCacheConfiguration(CacheNames.RESUME_CURRENT, resumeCache)
                .withCacheConfiguration(CacheNames.RESUME_PUBLIC, resumeCache);
    }
}
