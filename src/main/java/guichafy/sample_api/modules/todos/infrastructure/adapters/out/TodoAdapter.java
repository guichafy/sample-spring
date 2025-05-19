package guichafy.sample_api.modules.todos.infrastructure.adapters.out;

import guichafy.sample_api.modules.todos.application.port.out.TodoPort;
import guichafy.sample_api.modules.todos.domain.Todo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class TodoAdapter implements TodoPort {

    private final RestClient restClient;

    // Supondo que você terá um bean RestClient configurado com o nome "jsonplaceholderRestClient"
    // ou um RestClient padrão. Se for um específico, use @Qualifier.
    public TodoAdapter(@Qualifier("jsonplaceholderRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Todo> findByUserId(Long userId) {
        // Exemplo de chamada para https://jsonplaceholder.typicode.com/users/{userId}/todos
        return restClient.get()
                .uri("/users/{userId}/todos", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Todo>>() {});
    }

    @Override
    public Optional<Todo> findByIdAndUserId(Long todoId, Long userId) {
        // A API jsonplaceholder.typicode.com/todos/{todoId} não filtra por userId diretamente na URL.
        // Portanto, primeiro buscamos o todo por ID e depois verificamos se o userId corresponde.
        // Em uma API real, o endpoint poderia ser /users/{userId}/todos/{todoId}
        Todo todo = restClient.get()
                .uri("/todos/{todoId}", todoId)
                .retrieve()
                .body(Todo.class);

        if (todo != null && todo.getUserId().equals(userId)) {
            return Optional.of(todo);
        }
        return Optional.empty();
    }
}