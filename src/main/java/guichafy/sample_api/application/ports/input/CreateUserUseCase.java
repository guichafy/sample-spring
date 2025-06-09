package guichafy.sample_api.application.ports.input;

import guichafy.sample_api.domain.entities.User;

public interface CreateUserUseCase {
    User createUser(CreateUserCommand command);
}