package guichafy.sample_api.infrastructure.adapters.input.web;

import guichafy.sample_api.application.ports.input.CreateUserCommand;
import guichafy.sample_api.application.ports.input.CreateUserUseCase;
import guichafy.sample_api.application.ports.input.GetUserUseCase;
import guichafy.sample_api.domain.entities.User;
import guichafy.sample_api.domain.valueobjects.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = new CreateUserCommand(request.name(), request.email());
        User user = createUserUseCase.createUser(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        UserId userId = UserId.of(id);
        Optional<User> user = getUserUseCase.getUserById(userId);
        
        return user.map(u -> ResponseEntity.ok(UserResponse.fromUser(u)))
                  .orElse(ResponseEntity.notFound().build());
    }
}