package guichafy.sample_api.modules.todos.application;

import guichafy.sample_api.modules.todos.application.port.in.TodoUseCase;
import guichafy.sample_api.modules.todos.application.port.out.TodoPort;
import guichafy.sample_api.modules.todos.domain.Todo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoUseCase {

    private final TodoPort todoPort;

    public TodoServiceImpl(TodoPort todoPort) {
        this.todoPort = todoPort;
    }

    @Override
    public List<Todo> findTodosByUserId(Long userId) {
        // Lógica de negócios pode ser adicionada aqui se necessário
        return todoPort.findByUserId(userId);
    }

    @Override
    public Todo findTodoByIdAndUserId(Long todoId, Long userId) {
        // Lógica de negócios, como verificar se o todo pertence ao usuário, pode ser adicionada aqui
        return todoPort.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new RuntimeException("Todo not found")); // Exemplo de tratamento de Optional
    }
}