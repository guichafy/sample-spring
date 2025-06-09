package guichafy.sample_api.infrastructure.adapters.input.web;

import guichafy.sample_api.domain.entities.Todo;

public record TodoResponse(String id, Long userId, String title, boolean completed) {
    public static TodoResponse fromTodo(Todo todo) {
        return new TodoResponse(todo.id().value(), todo.userId(), todo.title(), todo.completed());
    }
}
