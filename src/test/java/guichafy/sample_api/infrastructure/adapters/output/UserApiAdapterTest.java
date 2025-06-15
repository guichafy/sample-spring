package guichafy.sample_api.infrastructure.adapters.output;

import guichafy.sample_api.domain.entities.User;
import guichafy.sample_api.domain.valueobjects.Email;
import guichafy.sample_api.domain.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApiAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private UserApiAdapter userApiAdapter;
    private final String baseUrl = "https://jsonplaceholder.typicode.com";

    @BeforeEach
    void setUp() {
        userApiAdapter = new UserApiAdapter(restTemplate, baseUrl);
    }

    @Test
    void shouldFindUserById() {
        UserId userId = UserId.of("1");
        UserApiAdapter.JsonPlaceholderUserDto mockDto = new UserApiAdapter.JsonPlaceholderUserDto(
            1L, "John Doe", "johndoe", "john.doe@example.com",
            new UserApiAdapter.AddressDto("Street", "Suite", "City", "12345", 
                new UserApiAdapter.GeoDto("0.0", "0.0")),
            "123-456-7890", "example.com",
            new UserApiAdapter.CompanyDto("Company", "Catchphrase", "BS")
        );
        
        when(restTemplate.getForObject(baseUrl + "/users/1", UserApiAdapter.JsonPlaceholderUserDto.class))
            .thenReturn(mockDto);

        Optional<User> result = userApiAdapter.findUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("1", result.get().id().value());
        assertEquals("John Doe", result.get().name());
        assertEquals("john.doe@example.com", result.get().email().value());
        assertNotNull(result.get().createdAt());
        assertNotNull(result.get().updatedAt());
        
        verify(restTemplate).getForObject(baseUrl + "/users/1", UserApiAdapter.JsonPlaceholderUserDto.class);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        UserId userId = UserId.of("999");
        
        when(restTemplate.getForObject(baseUrl + "/users/999", UserApiAdapter.JsonPlaceholderUserDto.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Optional<User> result = userApiAdapter.findUserById(userId);

        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(baseUrl + "/users/999", UserApiAdapter.JsonPlaceholderUserDto.class);
    }

    @Test
    void shouldReturnEmptyWhenUserDtoIsNull() {
        UserId userId = UserId.of("1");
        
        when(restTemplate.getForObject(baseUrl + "/users/1", UserApiAdapter.JsonPlaceholderUserDto.class))
            .thenReturn(null);

        Optional<User> result = userApiAdapter.findUserById(userId);

        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(baseUrl + "/users/1", UserApiAdapter.JsonPlaceholderUserDto.class);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFindUserByIdFailsWithNonNotFoundError() {
        UserId userId = UserId.of("1");
        
        when(restTemplate.getForObject(baseUrl + "/users/1", UserApiAdapter.JsonPlaceholderUserDto.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userApiAdapter.findUserById(userId)
        );

        assertEquals("Error fetching user from external API", exception.getMessage());
        verify(restTemplate).getForObject(baseUrl + "/users/1", UserApiAdapter.JsonPlaceholderUserDto.class);
    }

    @Test
    void shouldSaveUserAndPrintMessage() {
        User user = new User(
            UserId.of("123"),
            "John Doe",
            Email.of("john.doe@example.com"),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            User result = userApiAdapter.saveUser(user);

            assertEquals(user, result);
            String output = outputStream.toString();
            assertTrue(output.contains("Saving user (mock): John Doe - john.doe@example.com"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void shouldNotifyUserCreatedAndPrintMessage() {
        User user = new User(
            UserId.of("123"),
            "John Doe",
            Email.of("john.doe@example.com"),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            userApiAdapter.notifyUserCreated(user);

            String output = outputStream.toString();
            assertTrue(output.contains("User created notification: John Doe - john.doe@example.com"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        String email = "john.doe@example.com";
        UserApiAdapter.JsonPlaceholderUserDto[] mockUsers = {
            new UserApiAdapter.JsonPlaceholderUserDto(
                1L, "John Doe", "johndoe", "john.doe@example.com",
                null, null, null, null
            ),
            new UserApiAdapter.JsonPlaceholderUserDto(
                2L, "Jane Doe", "janedoe", "jane.doe@example.com",
                null, null, null, null
            )
        };
        
        when(restTemplate.getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class))
            .thenReturn(mockUsers);

        boolean result = userApiAdapter.emailExists(email);

        assertTrue(result);
        verify(restTemplate).getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        String email = "nonexistent@example.com";
        UserApiAdapter.JsonPlaceholderUserDto[] mockUsers = {
            new UserApiAdapter.JsonPlaceholderUserDto(
                1L, "John Doe", "johndoe", "john.doe@example.com",
                null, null, null, null
            )
        };
        
        when(restTemplate.getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class))
            .thenReturn(mockUsers);

        boolean result = userApiAdapter.emailExists(email);

        assertFalse(result);
        verify(restTemplate).getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class);
    }

    @Test
    void shouldReturnFalseWhenUsersArrayIsNull() {
        String email = "test@example.com";
        
        when(restTemplate.getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class))
            .thenReturn(null);

        boolean result = userApiAdapter.emailExists(email);

        assertFalse(result);
        verify(restTemplate).getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class);
    }

    @Test
    void shouldHandleCaseInsensitiveEmailComparison() {
        String email = "JOHN.DOE@EXAMPLE.COM";
        UserApiAdapter.JsonPlaceholderUserDto[] mockUsers = {
            new UserApiAdapter.JsonPlaceholderUserDto(
                1L, "John Doe", "johndoe", "john.doe@example.com",
                null, null, null, null
            )
        };
        
        when(restTemplate.getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class))
            .thenReturn(mockUsers);

        boolean result = userApiAdapter.emailExists(email);

        assertTrue(result);
        verify(restTemplate).getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenEmailExistsCheckFails() {
        String email = "test@example.com";
        
        when(restTemplate.getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class))
            .thenThrow(new RuntimeException("Network error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userApiAdapter.emailExists(email)
        );

        assertEquals("Error checking email existence in external API", exception.getMessage());
        verify(restTemplate).getForObject(baseUrl + "/users", UserApiAdapter.JsonPlaceholderUserDto[].class);
    }
}
