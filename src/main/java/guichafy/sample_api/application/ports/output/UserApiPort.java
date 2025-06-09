package guichafy.sample_api.application.ports.output;

import guichafy.sample_api.domain.entities.User;
import guichafy.sample_api.domain.valueobjects.UserId;

import java.util.Optional;

public interface UserApiPort {
    
    Optional<User> findUserById(UserId userId);
    
    User saveUser(User user);
    
    void notifyUserCreated(User user);
    
    boolean emailExists(String email);
}