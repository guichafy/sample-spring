package guichafy.sample_api.infrastructure.config;

import guichafy.sample_api.application.ports.output.UserApiPort;
import guichafy.sample_api.application.ports.output.TodoApiPort;
import guichafy.sample_api.application.usecases.UserService;
import guichafy.sample_api.application.usecases.TodoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserService userService(UserApiPort userApiPort) {
        return new UserService(userApiPort);
    }

    @Bean
    public TodoService todoService(TodoApiPort todoApiPort) {
        return new TodoService(todoApiPort);
    }
}