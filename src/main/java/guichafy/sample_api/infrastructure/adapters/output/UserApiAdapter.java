package guichafy.sample_api.infrastructure.adapters.output;

import guichafy.sample_api.application.ports.output.UserApiPort;
import guichafy.sample_api.domain.entities.User;
import guichafy.sample_api.domain.valueobjects.Email;
import guichafy.sample_api.domain.valueobjects.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class UserApiAdapter implements UserApiPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserApiAdapter(RestTemplate restTemplate, 
                         @Value("${api.jsonplaceholder.base-url:https://jsonplaceholder.typicode.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<User> findUserById(UserId userId) {
        try {
            String url = baseUrl + "/users/" + userId.value();
            JsonPlaceholderUserDto userDto = restTemplate.getForObject(url, JsonPlaceholderUserDto.class);
            
            if (userDto == null) {
                return Optional.empty();
            }
            
            return Optional.of(mapToUser(userDto));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new RuntimeException("Error fetching user from external API", e);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user from external API", e);
        }
    }

    @Override
    public User saveUser(User user) {
        // Para a implementação com API externa de leitura, 
        // não podemos salvar diretamente na API JSONPlaceholder
        // Retornamos o usuário como se fosse salvo (mock behavior)
        System.out.println("Saving user (mock): " + user.name() + " - " + user.email());
        return user;
    }

    @Override
    public void notifyUserCreated(User user) {
        System.out.println("User created notification: " + user.name() + " - " + user.email());
    }

    @Override
    public boolean emailExists(String email) {
        try {
            // Como a API JSONPlaceholder não tem endpoint para buscar por email,
            // vamos buscar todos os usuários (simulação simples)
            // Em uma implementação real, você teria um endpoint específico para isso
            String url = baseUrl + "/users";
            JsonPlaceholderUserDto[] users = restTemplate.getForObject(url, JsonPlaceholderUserDto[].class);
            
            if (users == null) {
                return false;
            }
            
            return java.util.Arrays.stream(users)
                    .anyMatch(user -> user.email() != null && user.email().equalsIgnoreCase(email));
        } catch (Exception e) {
            throw new RuntimeException("Error checking email existence in external API", e);
        }
    }

    private User mapToUser(JsonPlaceholderUserDto dto) {
        return new User(
            UserId.of(String.valueOf(dto.id())),
            dto.name(),
            Email.of(dto.email()),
            LocalDateTime.now(), // API externa não tem essa informação
            LocalDateTime.now()  // API externa não tem essa informação
        );
    }

    // DTO para mapear a resposta da API JSONPlaceholder
    public record JsonPlaceholderUserDto(
        Long id,
        String name,
        String username,
        String email,
        AddressDto address,
        String phone,
        String website,
        CompanyDto company
    ) {}

    public record AddressDto(
        String street,
        String suite,
        String city,
        String zipcode,
        GeoDto geo
    ) {}

    public record GeoDto(
        String lat,
        String lng
    ) {}

    public record CompanyDto(
        String name,
        String catchPhrase,
        String bs
    ) {}
}