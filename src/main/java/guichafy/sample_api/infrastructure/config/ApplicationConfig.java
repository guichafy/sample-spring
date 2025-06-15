package guichafy.sample_api.infrastructure.config;

import guichafy.sample_api.application.ports.output.RouteApiPort;
import guichafy.sample_api.application.ports.output.UserApiPort;
import guichafy.sample_api.application.ports.output.TodoApiPort;
import guichafy.sample_api.application.usecases.RouteService;
import guichafy.sample_api.application.usecases.UserService;
import guichafy.sample_api.application.usecases.TodoService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfig {

    /**
     * Configura RestTemplate com timeouts apropriados e Virtual Threads
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * Configura executor de tarefas assíncronas usando Virtual Threads
     * Disponível no Java 21+ com Spring Boot 3.2+
     */
    @Bean("virtualThreadTaskExecutor")
    public AsyncTaskExecutor virtualThreadTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public UserService userService(UserApiPort userApiPort) {
        return new UserService(userApiPort);
    }

    @Bean
    public TodoService todoService(TodoApiPort todoApiPort) {
        return new TodoService(todoApiPort);
    }

    @Bean
    public RouteService routeService(RouteApiPort routeApiPort) {
        return new RouteService(routeApiPort);
    }
}