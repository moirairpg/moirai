package es.thalesalv.gptbot.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OpenAIConfigurationBean {

    @Value("${config.openai.api-token}")
    private String openAiToken;

    @Bean
    public OpenAiService openAi() {

        return new OpenAiService(openAiToken);
    }
}
