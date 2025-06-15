package guichafy.sample_api.domain.entities;

import guichafy.sample_api.domain.valueobjects.TodoId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoTest {

    @Test
    void shouldCreateValidTodo() {
        TodoId todoId = TodoId.of("1");
        Long userId = 1L;
        String title = "Test Todo";
        boolean completed = false;

        Todo todo = new Todo(todoId, userId, title, completed);

        assertEquals(todoId, todo.id());
        assertEquals(userId, todo.userId());
        assertEquals(title, todo.title());
        assertEquals(completed, todo.completed());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        TodoId todoId = TodoId.of("1");
        String title = "Test Todo";
        boolean completed = false;

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Todo(todoId, null, title, completed)
        );

        assertEquals("UserId cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        TodoId todoId = TodoId.of("1");
        Long userId = 1L;
        boolean completed = false;

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Todo(todoId, userId, null, completed)
        );

        assertEquals("Title cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        TodoId todoId = TodoId.of("1");
        Long userId = 1L;
        String title = "";
        boolean completed = false;

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Todo(todoId, userId, title, completed)
        );

        assertEquals("Title cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        TodoId todoId = TodoId.of("1");
        Long userId = 1L;
        String title = "   ";
        boolean completed = false;

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Todo(todoId, userId, title, completed)
        );

        assertEquals("Title cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldCreateTodoWithCompletedTrue() {
        TodoId todoId = TodoId.of("1");
        Long userId = 1L;
        String title = "Completed Todo";
        boolean completed = true;

        Todo todo = new Todo(todoId, userId, title, completed);

        assertTrue(todo.completed());
    }

    @Test
    void shouldCreateTodoWithLongTitle() {
        TodoId todoId = TodoId.of("1");
        Long userId = 1L;
        String title = "This is a very long todo title that should still be valid as long as it's not null or empty";
        boolean completed = false;

        Todo todo = new Todo(todoId, userId, title, completed);

        assertEquals(title, todo.title());
    }
}
