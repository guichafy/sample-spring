package guichafy.sample_api.infrastructure.adapters.output;

import guichafy.sample_api.application.ports.output.TodoApiPort;
import guichafy.sample_api.domain.entities.Todo;
import guichafy.sample_api.domain.valueobjects.TodoId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TodoAdapter implements TodoApiPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public TodoAdapter(RestTemplate restTemplate,
                       @Value("${api.jsonplaceholder.base-url:https://jsonplaceholder.typicode.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Todo> findAllTodos() {
        try {
            String url = baseUrl + "/todos";
            JsonPlaceholderTodoDto[] dtos = restTemplate.getForObject(url, JsonPlaceholderTodoDto[].class);
            if (dtos == null) {
                return Collections.emptyList();
            }
            return Arrays.stream(dtos)
                    .map(this::mapToTodo)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching todos from external API", e);
        }
    }

    @Override
    public Optional<Todo> findTodoById(TodoId todoId) {
        try {
            String url = baseUrl + "/todos/" + todoId.value();
            JsonPlaceholderTodoDto dto = restTemplate.getForObject(url, JsonPlaceholderTodoDto.class);
            if (dto == null) {
                return Optional.empty();
            }
            return Optional.of(mapToTodo(dto));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new RuntimeException("Error fetching todo from external API", e);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching todo from external API", e);
        }
    }

    private Todo mapToTodo(JsonPlaceholderTodoDto dto) {
        return new Todo(
            TodoId.of(String.valueOf(dto.id())),
            dto.userId(),
            dto.title(),
            dto.completed()
        );
    }

    record JsonPlaceholderTodoDto(Long userId, Long id, String title, boolean completed) {}
}
