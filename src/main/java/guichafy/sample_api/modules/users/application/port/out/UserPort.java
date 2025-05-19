package guichafy.sample_api.modules.users.application.port.out;

import guichafy.sample_api.modules.users.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserPort {
    List<User> fetchAllUsers();
    Optional<User> fetchUserById(Long id);
}