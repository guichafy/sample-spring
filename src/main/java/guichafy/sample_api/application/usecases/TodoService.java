package guichafy.sample_api.application.usecases;

import guichafy.sample_api.application.ports.input.GetTodoUseCase;
import guichafy.sample_api.application.ports.input.GetTodosUseCase;
import guichafy.sample_api.application.ports.output.TodoApiPort;
import guichafy.sample_api.domain.entities.Todo;
import guichafy.sample_api.domain.valueobjects.TodoId;

import java.util.List;
import java.util.Optional;

public class TodoService implements GetTodoUseCase, GetTodosUseCase {

    private final TodoApiPort todoApiPort;

    public TodoService(TodoApiPort todoApiPort) {
        this.todoApiPort = todoApiPort;
    }

    @Override
    public Optional<Todo> getTodoById(TodoId todoId) {
        return todoApiPort.findTodoById(todoId);
    }

    @Override
    public List<Todo> getTodos() {
        return todoApiPort.findAllTodos();
    }
}
