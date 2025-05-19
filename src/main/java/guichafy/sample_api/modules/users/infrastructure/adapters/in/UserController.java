package guichafy.sample_api.modules.users.infrastructure.adapters.in;

import guichafy.sample_api.common.representation.ApiResponse;
import guichafy.sample_api.modules.todos.infrastructure.adapters.in.TodoController;
import guichafy.sample_api.modules.users.application.port.in.UserUseCase;
import guichafy.sample_api.modules.users.domain.User;
import guichafy.sample_api.modules.users.infrastructure.representation.UserResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    private UserResponse toUserResponse(User user) {
        // Mapeamento simples. Em um cenário real, use MapStruct ou similar.
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAddress(user.getAddress());
        response.setPhone(user.getPhone());
        response.setWebsite(user.getWebsite());
        response.setCompany(user.getCompany());
        return response;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CollectionModel<EntityModel<UserResponse>>>> getAllUsers() {
        List<UserResponse> userResponses = userUseCase.getAllUsers().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        List<EntityModel<UserResponse>> userEntities = userResponses.stream()
                .map(userResp -> {
                    try {
                        Link selfLink = linkTo(methodOn(UserController.class).getUserById(userResp.getId())).withSelfRel();
                        Link todosLink = linkTo(methodOn(TodoController.class).getTodosByUserId(userResp.getId())).withRel("todos");
                        return EntityModel.of(userResp, selfLink, todosLink);
                    } catch (Exception e) {
                        // Log.error("Error creating HATEOAS links for user: " + userResp.getId(), e);
                        return EntityModel.of(userResp); // Fallback em caso de erro
                    }
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserResponse>> collectionModel;
        try {
            collectionModel = CollectionModel.of(userEntities,
                    linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        } catch (Exception e) {
            // Log.error("Error creating collection model for users", e);
            collectionModel = CollectionModel.of(userEntities); // Fallback
        }
        return ResponseEntity.ok(new ApiResponse<>(collectionModel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EntityModel<UserResponse>>> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userUseCase.getUserById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserResponse userResponse = toUserResponse(userOptional.get());
        EntityModel<UserResponse> entityModel;
        try {
            Link selfLink = linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel();
            Link todosLink = linkTo(methodOn(TodoController.class).getTodosByUserId(id)).withRel("todos");
            Link allUsersLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");
            entityModel = EntityModel.of(userResponse, selfLink, todosLink, allUsersLink);
        } catch (Exception e) {
            // Log.error("Error creating HATEOAS links for user: " + id, e);
            entityModel = EntityModel.of(userResponse); // Fallback
        }

        return ResponseEntity.ok(new ApiResponse<>(entityModel));
    }
}