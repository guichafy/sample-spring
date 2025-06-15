package guichafy.sample_api.infrastructure.adapters.output;

import guichafy.sample_api.domain.entities.Todo;
import guichafy.sample_api.domain.valueobjects.TodoId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private TodoAdapter todoAdapter;
    private final String baseUrl = "https://jsonplaceholder.typicode.com";

    @BeforeEach
    void setUp() {
        todoAdapter = new TodoAdapter(restTemplate, baseUrl);
    }

    @Test
    void shouldFindAllTodos() {
        TodoAdapter.JsonPlaceholderTodoDto[] mockDtos = {
            new TodoAdapter.JsonPlaceholderTodoDto(1L, 1L, "Todo 1", false),
            new TodoAdapter.JsonPlaceholderTodoDto(1L, 2L, "Todo 2", true),
            new TodoAdapter.JsonPlaceholderTodoDto(2L, 3L, "Todo 3", false)
        };
        
        when(restTemplate.getForObject(baseUrl + "/todos", TodoAdapter.JsonPlaceholderTodoDto[].class))
            .thenReturn(mockDtos);

        List<Todo> result = todoAdapter.findAllTodos();

        assertEquals(3, result.size());
        assertEquals("1", result.get(0).id().value());
        assertEquals("Todo 1", result.get(0).title());
        assertEquals(1L, result.get(0).userId());
        assertFalse(result.get(0).completed());
        
        assertEquals("2", result.get(1).id().value());
        assertEquals("Todo 2", result.get(1).title());
        assertTrue(result.get(1).completed());
        
        verify(restTemplate).getForObject(baseUrl + "/todos", TodoAdapter.JsonPlaceholderTodoDto[].class);
    }

    @Test
    void shouldReturnEmptyListWhenNoDtos() {
        when(restTemplate.getForObject(baseUrl + "/todos", TodoAdapter.JsonPlaceholderTodoDto[].class))
            .thenReturn(null);

        List<Todo> result = todoAdapter.findAllTodos();

        assertTrue(result.isEmpty());
        verify(restTemplate).getForObject(baseUrl + "/todos", TodoAdapter.JsonPlaceholderTodoDto[].class);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFindAllTodosFails() {
        when(restTemplate.getForObject(baseUrl + "/todos", TodoAdapter.JsonPlaceholderTodoDto[].class))
            .thenThrow(new RuntimeException("Network error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> todoAdapter.findAllTodos()
        );

        assertEquals("Error fetching todos from external API", exception.getMessage());
        verify(restTemplate).getForObject(baseUrl + "/todos", TodoAdapter.JsonPlaceholderTodoDto[].class);
    }

    @Test
    void shouldFindTodoById() {
        TodoId todoId = TodoId.of("1");
        TodoAdapter.JsonPlaceholderTodoDto mockDto = 
            new TodoAdapter.JsonPlaceholderTodoDto(1L, 1L, "Test Todo", false);
        
        when(restTemplate.getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class))
            .thenReturn(mockDto);

        Optional<Todo> result = todoAdapter.findTodoById(todoId);

        assertTrue(result.isPresent());
        assertEquals("1", result.get().id().value());
        assertEquals("Test Todo", result.get().title());
        assertEquals(1L, result.get().userId());
        assertFalse(result.get().completed());
        
        verify(restTemplate).getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class);
    }

    @Test
    void shouldReturnEmptyWhenTodoNotFound() {
        TodoId todoId = TodoId.of("999");
        
        when(restTemplate.getForObject(baseUrl + "/todos/999", TodoAdapter.JsonPlaceholderTodoDto.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Optional<Todo> result = todoAdapter.findTodoById(todoId);

        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(baseUrl + "/todos/999", TodoAdapter.JsonPlaceholderTodoDto.class);
    }

    @Test
    void shouldReturnEmptyWhenDtoIsNull() {
        TodoId todoId = TodoId.of("1");
        
        when(restTemplate.getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class))
            .thenReturn(null);

        Optional<Todo> result = todoAdapter.findTodoById(todoId);

        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFindTodoByIdFailsWithNonNotFoundError() {
        TodoId todoId = TodoId.of("1");
        
        when(restTemplate.getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> todoAdapter.findTodoById(todoId)
        );

        assertEquals("Error fetching todo from external API", exception.getMessage());
        verify(restTemplate).getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFindTodoByIdFailsWithGenericException() {
        TodoId todoId = TodoId.of("1");
        
        when(restTemplate.getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class))
            .thenThrow(new RuntimeException("Network error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> todoAdapter.findTodoById(todoId)
        );

        assertEquals("Error fetching todo from external API", exception.getMessage());
        verify(restTemplate).getForObject(baseUrl + "/todos/1", TodoAdapter.JsonPlaceholderTodoDto.class);
    }
}
