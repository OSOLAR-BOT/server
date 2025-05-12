package capstone.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${external.gemini.api-key}")
    private String apiKey;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta") // Gemini API 기본 URL
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey) // API 키 추가
                .build();
    }

}

