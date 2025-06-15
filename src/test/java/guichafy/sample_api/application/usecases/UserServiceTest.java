package guichafy.sample_api.application.usecases;

import guichafy.sample_api.application.ports.input.CreateUserCommand;
import guichafy.sample_api.application.ports.output.UserApiPort;
import guichafy.sample_api.domain.entities.User;
import guichafy.sample_api.domain.valueobjects.Email;
import guichafy.sample_api.domain.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserApiPort userApiPort;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userApiPort);
    }

    @Test
    void shouldCreateUser() {
        String name = "John Doe";
        String emailValue = "john.doe@example.com";
        CreateUserCommand command = new CreateUserCommand(name, emailValue);
        
        when(userApiPort.emailExists(emailValue)).thenReturn(false);
        
        User savedUser = new User(
            UserId.generate(),
            name,
            Email.of(emailValue),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(userApiPort.saveUser(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(command);

        assertNotNull(result);
        assertEquals(name, result.name());
        assertEquals(emailValue, result.email().value());
        verify(userApiPort).emailExists(emailValue);
        verify(userApiPort).saveUser(any(User.class));
        verify(userApiPort).notifyUserCreated(savedUser);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        String name = "John Doe";
        String emailValue = "existing@example.com";
        CreateUserCommand command = new CreateUserCommand(name, emailValue);
        
        when(userApiPort.emailExists(emailValue)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(command)
        );

        assertEquals("User with email already exists", exception.getMessage());
        verify(userApiPort).emailExists(emailValue);
        verify(userApiPort, never()).saveUser(any(User.class));
        verify(userApiPort, never()).notifyUserCreated(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        String name = "John Doe";
        String invalidEmail = "invalid-email";
        CreateUserCommand command = new CreateUserCommand(name, invalidEmail);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(command)
        );

        assertEquals("Invalid email format", exception.getMessage());
        verify(userApiPort, never()).emailExists(anyString());
        verify(userApiPort, never()).saveUser(any(User.class));
        verify(userApiPort, never()).notifyUserCreated(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        UserId userId = UserId.of("123");
        User expectedUser = new User(
            userId,
            "John Doe",
            Email.of("john.doe@example.com"),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(userApiPort.findUserById(userId)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userApiPort).findUserById(userId);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        UserId userId = UserId.of("999");
        
        when(userApiPort.findUserById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());
        verify(userApiPort).findUserById(userId);
    }

    @Test
    void shouldCreateUserWithGeneratedId() {
        String name = "Jane Doe";
        String emailValue = "jane.doe@example.com";
        CreateUserCommand command = new CreateUserCommand(name, emailValue);
        
        when(userApiPort.emailExists(emailValue)).thenReturn(false);
        when(userApiPort.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(command);

        assertNotNull(result.id());
        assertNotNull(result.id().value());
        assertFalse(result.id().value().isEmpty());
        verify(userApiPort).saveUser(any(User.class));
    }

    @Test
    void shouldSetCreatedAndUpdatedTimestamps() {
        String name = "Test User";
        String emailValue = "test@example.com";
        CreateUserCommand command = new CreateUserCommand(name, emailValue);
        
        when(userApiPort.emailExists(emailValue)).thenReturn(false);
        when(userApiPort.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        User result = userService.createUser(command);
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        assertTrue(result.createdAt().isAfter(beforeCreation));
        assertTrue(result.createdAt().isBefore(afterCreation));
        assertTrue(result.updatedAt().isAfter(beforeCreation));
        assertTrue(result.updatedAt().isBefore(afterCreation));
    }
}
