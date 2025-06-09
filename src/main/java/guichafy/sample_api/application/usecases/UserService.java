package guichafy.sample_api.application.usecases;

import guichafy.sample_api.application.ports.input.CreateUserCommand;
import guichafy.sample_api.application.ports.input.CreateUserUseCase;
import guichafy.sample_api.application.ports.input.GetUserUseCase;
import guichafy.sample_api.application.ports.output.UserApiPort;
import guichafy.sample_api.domain.entities.User;
import guichafy.sample_api.domain.valueobjects.Email;
import guichafy.sample_api.domain.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.Optional;

public class UserService implements CreateUserUseCase, GetUserUseCase {

    private final UserApiPort userApiPort;

    public UserService(UserApiPort userApiPort) {
        this.userApiPort = userApiPort;
    }

    @Override
    public User createUser(CreateUserCommand command) {
        Email email = Email.of(command.email());
        
        if (userApiPort.emailExists(command.email())) {
            throw new IllegalArgumentException("User with email already exists");
        }

        User newUser = new User(
            UserId.generate(),
            command.name(),
            email,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        User savedUser = userApiPort.saveUser(newUser);
        userApiPort.notifyUserCreated(savedUser);
        
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(UserId userId) {
        return userApiPort.findUserById(userId);
    }
}