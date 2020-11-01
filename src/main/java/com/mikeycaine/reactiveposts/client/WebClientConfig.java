package com.mikeycaine.reactiveposts.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final String HTTPS_FORUMS_SOMETHINGAWFUL_COM = "https://forums.somethingawful.com/";
    private static final int MAX_WEBCLIENT_CODEC_MEMORY_SIZE = 16 * 1024 * 1024;

    @Bean
    public WebClient webClient() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_WEBCLIENT_CODEC_MEMORY_SIZE))
            .build();

        return WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .baseUrl(HTTPS_FORUMS_SOMETHINGAWFUL_COM)
            .build();
    }
}
