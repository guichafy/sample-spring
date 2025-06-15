package guichafy.sample_api.infrastructure.adapters.input.web;

import guichafy.sample_api.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class TodoControllerIntegrationTest {

    @Container
    static GenericContainer<?> wiremock = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:3.9.1"))
            .withExposedPorts(8080)
            .withCommand("--disable-banner")
            .waitingFor(Wait.forHttp("/__admin/health")
                .forPort(8080)
                .withStartupTimeout(Duration.ofMinutes(2)));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    private String wiremockUrl;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.jsonplaceholder.base-url", () -> 
            String.format("http://%s:%d", wiremock.getHost(), wiremock.getMappedPort(8080)));
    }

    @BeforeEach
    void setup() {
        wiremockUrl = String.format("http://%s:%d", 
            wiremock.getHost(), 
            wiremock.getMappedPort(8080));
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        setupWireMockStubs();
    }
    
    private void setupWireMockStubs() {
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
                  {"userId": 1, "id": 2, "title": "quis ut nam facilis et officia qui", "completed": false},
                  {"userId": 2, "id": 3, "title": "fugiat veniam minus", "completed": false}
                ]
              }
            }
            """;
            
        restTemplate.postForEntity(
            wiremockUrl + "/__admin/mappings",
            todosStub,
            String.class
        );
        
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
            
        restTemplate.postForEntity(
            wiremockUrl + "/__admin/mappings",
            todoStub,
            String.class
        );

        String notFoundStub = """
            {
              "request": {
                "method": "GET",
                "url": "/todos/999"
              },
              "response": {
                "status": 404
              }
            }
            """;
            
        restTemplate.postForEntity(
            wiremockUrl + "/__admin/mappings",
            notFoundStub,
            String.class
        );
    }

    @Test
    void shouldGetAllTodos() throws Exception {
        mockMvc.perform(get("/api/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].title").value("delectus aut autem"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[2].id").value("3"));
    }

    @Test
    void shouldGetTodoById() throws Exception {
        mockMvc.perform(get("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.title").value("delectus aut autem"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void shouldReturnNotFoundForNonExistentTodo() throws Exception {
        mockMvc.perform(get("/api/todos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleInvalidTodoId() throws Exception {
        mockMvc.perform(get("/api/todos/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
