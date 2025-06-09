package guichafy.sample_api.application.ports.output;

import guichafy.sample_api.domain.entities.Todo;
import guichafy.sample_api.domain.valueobjects.TodoId;

import java.util.List;
import java.util.Optional;

public interface TodoApiPort {

    List<Todo> findAllTodos();

    Optional<Todo> findTodoById(TodoId todoId);
}
