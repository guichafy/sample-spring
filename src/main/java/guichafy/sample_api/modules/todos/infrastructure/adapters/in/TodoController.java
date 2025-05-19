package guichafy.sample_api.modules.todos.infrastructure.adapters.in;

import guichafy.sample_api.common.representation.ApiResponse;
import guichafy.sample_api.modules.todos.infrastructure.representation.TodoResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import guichafy.sample_api.modules.todos.application.port.in.TodoUseCase;
import guichafy.sample_api.modules.todos.domain.Todo; // Adicionar import para Todo (domain)
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users/{userId}/todos") // Exemplo de rota
public class TodoController {

    private final TodoUseCase todoUseCase;

    public TodoController(TodoUseCase todoUseCase) {
        this.todoUseCase = todoUseCase;
    }

    private TodoResponse toTodoResponse(Todo todo) {
        TodoResponse response = new TodoResponse(todo.getUserId(), todo.getId(), todo.getTitle(), todo.isCompleted());
        // Adicionar links HATEOAS ao TodoResponse individual
        try {
            Link selfLink = linkTo(methodOn(TodoController.class).getTodoById(todo.getUserId(), todo.getId())).withSelfRel();
            response.add(selfLink);
            // Link para a coleção de todos do usuário pode ser adicionado aqui se fizer sentido no contexto individual
            // Link todosLink = linkTo(methodOn(TodoController.class).getTodosByUserId(todo.getUserId())).withRel("user-todos");
            // response.add(todosLink);
        } catch (Exception e) {
            // Tratar exceções na criação de links, se necessário (ex: IllegalStateException)
            // Log.error("Error creating HATEOAS links for TodoResponse", e);
        }
        return response;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<CollectionModel<EntityModel<TodoResponse>>>> getTodosByUserId(@PathVariable Long userId) {
        List<Todo> todosDomain = todoUseCase.findTodosByUserId(userId);

        List<EntityModel<TodoResponse>> todoEntities = todosDomain.stream()
                .map(this::toTodoResponse)
                .map(todoResp -> {
                    try {
                        return EntityModel.of(todoResp,
                                linkTo(methodOn(TodoController.class).getTodoById(userId, todoResp.getId())).withSelfRel());
                    } catch (Exception e) {
                        // Log.error("Error creating entity model for todo: " + todoResp.getId(), e);
                        return EntityModel.of(todoResp); // Fallback sem link em caso de erro
                    }
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<TodoResponse>> collectionModel;
        try {
            collectionModel = CollectionModel.of(todoEntities,
                    linkTo(methodOn(TodoController.class).getTodosByUserId(userId)).withSelfRel());
        } catch (Exception e) {
            // Log.error("Error creating collection model for todos of user: " + userId, e);
            collectionModel = CollectionModel.of(todoEntities); // Fallback sem link self para a coleção
        }

        return ResponseEntity.ok(new ApiResponse<>(collectionModel));
    }

     @GetMapping("/{todoId}")
    public ResponseEntity<ApiResponse<EntityModel<TodoResponse>>> getTodoById(@PathVariable Long userId, @PathVariable Long todoId) {
        try {
            Todo todoDomain = todoUseCase.findTodoByIdAndUserId(todoId, userId);
            TodoResponse todoResponse = toTodoResponse(todoDomain); // toTodoResponse já adiciona o self-link

            EntityModel<TodoResponse> entityModel = EntityModel.of(todoResponse);
            // Adicionar link para a coleção de todos do usuário
            try {
                Link userTodosLink = linkTo(methodOn(TodoController.class).getTodosByUserId(userId)).withRel("user-todos");
                entityModel.add(userTodosLink);
            } catch (Exception e) {
                // Log.error("Error creating user-todos link for todo: " + todoId, e);
            }
            
            return ResponseEntity.ok(new ApiResponse<>(entityModel));
        } catch (RuntimeException e) { // Captura a RuntimeException de TodoServiceImpl
            // Idealmente, usar uma exceção mais específica e um @ControllerAdvice
            // Log.error("Todo not found with id: " + todoId + " for user: " + userId, e);
            return ResponseEntity.notFound().build();
        }
    }
}