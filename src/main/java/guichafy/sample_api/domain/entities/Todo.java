package guichafy.sample_api.domain.entities;

import guichafy.sample_api.domain.valueobjects.TodoId;

public record Todo(
    TodoId id,
    Long userId,
    String title,
    boolean completed
) {
    public Todo {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
    }
}
