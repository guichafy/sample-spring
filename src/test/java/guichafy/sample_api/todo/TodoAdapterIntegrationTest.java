package guichafy.sample_api.todo;

import guichafy.sample_api.infrastructure.adapters.output.TodoAdapter;
import guichafy.sample_api.domain.valueobjects.TodoId;
import guichafy.sample_api.domain.entities.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class TodoAdapterIntegrationTest {

    @Container
    static GenericContainer<?> wiremock = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:3.9.1"))
            .withExposedPorts(8080)
            .withCommand("--disable-banner")
            .waitingFor(Wait.forHttp("/__admin/health")
                .forPort(8080)
                .withStartupTimeout(Duration.ofMinutes(2)));

    private TodoAdapter todoAdapter;
    private String wiremockUrl;

    @BeforeEach
    void setup() {
        wiremockUrl = String.format("http://%s:%d", 
            wiremock.getHost(), 
            wiremock.getMappedPort(8080));
        
        RestTemplate restTemplate = new RestTemplate();
        todoAdapter = new TodoAdapter(restTemplate, wiremockUrl);
        
        // Wait a bit more for WireMock to be fully ready
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Setup WireMock mappings programmatically
        setupWireMockStubs();
    }
    
    private void setupWireMockStubs() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Setup stub for GET /todos
        String todosStub = """
            {
              "request": {
                "method": "GET",
                "url": "/todos"
              },
              "response": {
                "status": 200,
                "headers": {"Content-Type": "application/json"},
                "jsonBody": [
                  {"userId": 1, "id": 1, "title": "delectus aut autem", "completed": false},
                  {"userId": 1, "id": 2, "title": "quis ut nam facilis et officia qui", "completed": false}
                ]
              }
            }
            """;
            
        HttpEntity<String> todosRequest = new HttpEntity<>(todosStub, headers);
        ResponseEntity<String> todosResponse = testRestTemplate.postForEntity(
            wiremockUrl + "/__admin/mappings",
            todosRequest,
            String.class
        );
        
        assertEquals(HttpStatus.CREATED, todosResponse.getStatusCode());
        
        // Setup stub for GET /todos/1
        String todoStub = """
            {
              "request": {
                "method": "GET",
                "url": "/todos/1"
              },
              "response": {
                "status": 200,
                "headers": {"Content-Type": "application/json"},
                "jsonBody": {
                  "userId": 1,
                  "id": 1,
                  "title": "delectus aut autem",
                  "completed": false
                }
              }
            }
            """;
            
        HttpEntity<String> todoRequest = new HttpEntity<>(todoStub, headers);
        ResponseEntity<String> todoResponse = testRestTemplate.postForEntity(
            wiremockUrl + "/__admin/mappings",
            todoRequest,
            String.class
        );
        
        assertEquals(HttpStatus.CREATED, todoResponse.getStatusCode());
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
