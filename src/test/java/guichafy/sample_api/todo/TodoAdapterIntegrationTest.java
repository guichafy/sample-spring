package guichafy.sample_api.todo;

import guichafy.sample_api.infrastructure.adapters.output.TodoAdapter;
import guichafy.sample_api.domain.valueobjects.TodoId;
import guichafy.sample_api.domain.entities.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class TodoAdapterIntegrationTest {

    @Container
    static WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock:3.3.1")
            .withMappingFromResource("todos.json")
            .withMappingFromResource("todo-1.json");

    private TodoAdapter todoAdapter;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = new RestTemplate();
        todoAdapter = new TodoAdapter(restTemplate, wiremock.getBaseUrl());
    }

    @Test
    void shouldFetchAllTodos() {
        List<Todo> todos = todoAdapter.findAllTodos();
        assertEquals(2, todos.size());
        assertEquals("delectus aut autem", todos.get(0).title());
    }

    @Test
    void shouldFetchTodoById() {
        Optional<Todo> todo = todoAdapter.findTodoById(TodoId.of("1"));
        assertTrue(todo.isPresent());
        assertEquals("delectus aut autem", todo.get().title());
    }
}
