package guichafy.sample_api.modules.todos.application.port.in;

import guichafy.sample_api.modules.todos.domain.Todo;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import guichafy.sample_api.modules.todos.infrastructure.representation.TodoResponse;

import java.util.List;

public interface TodoUseCase {
    List<Todo> findTodosByUserId(Long userId);
    Todo findTodoByIdAndUserId(Long todoId, Long userId);
    // Métodos para criar, atualizar, deletar Todos podem ser adicionados aqui
}