package com.penguin.linknote.domain.resources;

import lombok.Data;

@Data
public class ResourcesFilter {
    private String title;

    /**
     * Nuxt runtime parameters (mirrors Redis connection settings used in nuxt.config.ts).
     */
    private NuxtParams nuxt;

    @Data
    public static class NuxtParams {
        private String redisHost;
        private Integer redisPort;
        private String redisUsername;
        private String redisPassword;
        private Integer redisDb;
    }
}
