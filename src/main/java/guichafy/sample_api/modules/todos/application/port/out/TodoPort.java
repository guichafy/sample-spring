package guichafy.sample_api.modules.todos.application.port.out;

import guichafy.sample_api.modules.todos.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoPort {
    List<Todo> findByUserId(Long userId);
    Optional<Todo> findByIdAndUserId(Long todoId, Long userId);
    // Métodos para interagir com a fonte de dados externa para criar, atualizar, deletar Todos podem ser adicionados aqui
}