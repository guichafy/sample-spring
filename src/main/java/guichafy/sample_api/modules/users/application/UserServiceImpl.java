package guichafy.sample_api.modules.users.application;

import guichafy.sample_api.modules.users.application.port.in.UserUseCase;
import guichafy.sample_api.modules.users.application.port.out.UserPort;
import guichafy.sample_api.modules.users.domain.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserUseCase {

    private final UserPort userPort;

    public UserServiceImpl(UserPort userPort) {
        this.userPort = userPort;
    }

    @Override
    public List<User> getAllUsers() {
        return userPort.fetchAllUsers();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userPort.fetchUserById(id);
    }
}