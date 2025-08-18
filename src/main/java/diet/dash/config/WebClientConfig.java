package diet.dash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final OpenAIConfig openAIConfig;

    public WebClientConfig(OpenAIConfig openAIConfig) {
        this.openAIConfig = openAIConfig;
    }

    @Bean("openAiWebClient")
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl(openAIConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAIConfig.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
