package com.contestpulse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Exposes the RestClient(s) used to call external contest platforms.
 *
 * RestClient is Spring's modern synchronous HTTP client (added in Spring
 * Framework 6.1 / Spring Boot 3.2), already available through
 * spring-boot-starter-web -- no new Maven dependency was needed for this.
 *
 * The bean is named/scoped specifically to Codeforces (base URL baked in)
 * rather than one generic "restClient" bean, because the next platform
 * (LeetCode, CodeChef, ...) will need a different base URL. Each provider
 * gets its own RestClient bean here rather than one shared bean with no
 * base URL, so every provider's HTTP calls stay short (relative paths only).
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient codeforcesRestClient() {
        return RestClient.builder()
                .baseUrl("https://codeforces.com/api")
                .build();
    }
}
