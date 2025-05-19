package guichafy.sample_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${external.api.jsonplaceholder.base-url:https://jsonplaceholder.typicode.com}")
    private String jsonplaceholderBaseUrl;

    @Bean
    public RestClient jsonplaceholderRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(jsonplaceholderBaseUrl)
                .build();
    }
}