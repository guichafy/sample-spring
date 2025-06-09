package guichafy.sample_api.application.ports.input;

import guichafy.sample_api.domain.entities.User;
import guichafy.sample_api.domain.valueobjects.UserId;

import java.util.Optional;

public interface GetUserUseCase {
    Optional<User> getUserById(UserId userId);
}