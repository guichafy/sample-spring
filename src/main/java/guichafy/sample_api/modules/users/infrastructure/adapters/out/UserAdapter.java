package guichafy.sample_api.modules.users.infrastructure.adapters.out;

import guichafy.sample_api.modules.users.application.port.out.UserPort;
import guichafy.sample_api.modules.users.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Optional;

@Component
public class UserAdapter implements UserPort {

    private final RestClient restClient;

    public UserAdapter(@Qualifier("jsonplaceholderRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<User> fetchAllUsers() {
        return restClient.get()
                .uri("/users")
                .retrieve()
                .body(new ParameterizedTypeReference<List<User>>() {});
    }

    @Override
    public Optional<User> fetchUserById(Long id) {
        User user = restClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .body(User.class);
        return Optional.ofNullable(user);
    }
}