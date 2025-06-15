package guichafy.sample_api.application.usecases;

import guichafy.sample_api.application.ports.output.TodoApiPort;
import guichafy.sample_api.domain.entities.Todo;
import guichafy.sample_api.domain.valueobjects.TodoId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoApiPort todoApiPort;

    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService(todoApiPort);
    }

    @Test
    void shouldGetTodoById() {
        TodoId todoId = TodoId.of("1");
        Todo expectedTodo = new Todo(todoId, 1L, "Test Todo", false);
        
        when(todoApiPort.findTodoById(todoId)).thenReturn(Optional.of(expectedTodo));

        Optional<Todo> result = todoService.getTodoById(todoId);

        assertTrue(result.isPresent());
        assertEquals(expectedTodo, result.get());
        verify(todoApiPort).findTodoById(todoId);
    }

    @Test
    void shouldReturnEmptyWhenTodoNotFound() {
        TodoId todoId = TodoId.of("999");
        
        when(todoApiPort.findTodoById(todoId)).thenReturn(Optional.empty());

        Optional<Todo> result = todoService.getTodoById(todoId);

        assertFalse(result.isPresent());
        verify(todoApiPort).findTodoById(todoId);
    }

    @Test
    void shouldGetAllTodos() {
        List<Todo> expectedTodos = Arrays.asList(
            new Todo(TodoId.of("1"), 1L, "Todo 1", false),
            new Todo(TodoId.of("2"), 1L, "Todo 2", true),
            new Todo(TodoId.of("3"), 2L, "Todo 3", false)
        );
        
        when(todoApiPort.findAllTodos()).thenReturn(expectedTodos);

        List<Todo> result = todoService.getTodos();

        assertEquals(3, result.size());
        assertEquals(expectedTodos, result);
        verify(todoApiPort).findAllTodos();
    }

    @Test
    void shouldReturnEmptyListWhenNoTodos() {
        when(todoApiPort.findAllTodos()).thenReturn(Collections.emptyList());

        List<Todo> result = todoService.getTodos();

        assertTrue(result.isEmpty());
        verify(todoApiPort).findAllTodos();
    }

    @Test
    void shouldHandleNullTodoId() {
        TodoId todoId = null;
        
        when(todoApiPort.findTodoById(todoId)).thenReturn(Optional.empty());

        Optional<Todo> result = todoService.getTodoById(todoId);

        assertFalse(result.isPresent());
        verify(todoApiPort).findTodoById(todoId);
    }
}
