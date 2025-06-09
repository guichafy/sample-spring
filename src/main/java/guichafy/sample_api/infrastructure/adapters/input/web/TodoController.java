package guichafy.sample_api.infrastructure.adapters.input.web;

import guichafy.sample_api.application.ports.input.GetTodoUseCase;
import guichafy.sample_api.application.ports.input.GetTodosUseCase;
import guichafy.sample_api.domain.entities.Todo;
import guichafy.sample_api.domain.valueobjects.TodoId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final GetTodosUseCase getTodosUseCase;
    private final GetTodoUseCase getTodoUseCase;

    public TodoController(GetTodosUseCase getTodosUseCase, GetTodoUseCase getTodoUseCase) {
        this.getTodosUseCase = getTodosUseCase;
        this.getTodoUseCase = getTodoUseCase;
    }

    @GetMapping
    public List<TodoResponse> getTodos() {
        return getTodosUseCase.getTodos().stream()
                .map(TodoResponse::fromTodo)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable String id) {
        Optional<Todo> todo = getTodoUseCase.getTodoById(TodoId.of(id));
        return todo.map(t -> ResponseEntity.ok(TodoResponse.fromTodo(t)))
                .orElse(ResponseEntity.notFound().build());
    }
}
