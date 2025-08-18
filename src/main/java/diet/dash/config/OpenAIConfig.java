package diet.dash.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class OpenAIConfig {

    @Value("${openai.api.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model:gpt-4o-mini}")
    private String model;

}
