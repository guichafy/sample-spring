package guichafy.sample_api.modules.users.application.port.in;

import guichafy.sample_api.modules.users.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserUseCase {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
}