package guichafy.sample_api.application.ports.input;

import guichafy.sample_api.domain.entities.Todo;
import guichafy.sample_api.domain.valueobjects.TodoId;

import java.util.Optional;

public interface GetTodoUseCase {
    Optional<Todo> getTodoById(TodoId todoId);
}
