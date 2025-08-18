package diet.dash.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean("openAiWebClient")
    @Primary
    public WebClient openAiWebClient() {
        return mock(WebClient.class);
    }

    @Bean
    @Primary
    public OpenAIConfig openAIConfig() {
        return mock(OpenAIConfig.class);
    }
}
