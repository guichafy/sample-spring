package guichafy.sample_api.domain.entities;

import guichafy.sample_api.domain.valueobjects.Email;
import guichafy.sample_api.domain.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateValidUser() {
        UserId userId = UserId.of("123");
        String name = "John Doe";
        Email email = Email.of("john.doe@example.com");
        LocalDateTime now = LocalDateTime.now();

        User user = new User(userId, name, email, now, now);

        assertEquals(userId, user.id());
        assertEquals(name, user.name());
        assertEquals(email, user.email());
        assertEquals(now, user.createdAt());
        assertEquals(now, user.updatedAt());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        UserId userId = UserId.of("123");
        Email email = Email.of("john.doe@example.com");
        LocalDateTime now = LocalDateTime.now();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new User(userId, null, email, now, now)
        );

        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        UserId userId = UserId.of("123");
        String name = "";
        Email email = Email.of("john.doe@example.com");
        LocalDateTime now = LocalDateTime.now();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new User(userId, name, email, now, now)
        );

        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        UserId userId = UserId.of("123");
        String name = "   ";
        Email email = Email.of("john.doe@example.com");
        LocalDateTime now = LocalDateTime.now();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new User(userId, name, email, now, now)
        );

        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        UserId userId = UserId.of("123");
        String name = "John Doe";
        LocalDateTime now = LocalDateTime.now();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new User(userId, name, null, now, now)
        );

        assertEquals("Email cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateNameWithNewTimestamp() {
        UserId userId = UserId.of("123");
        String originalName = "John Doe";
        Email email = Email.of("john.doe@example.com");
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusHours(1);

        User originalUser = new User(userId, originalName, email, createdAt, originalUpdatedAt);
        
        String newName = "Jane Doe";
        User updatedUser = originalUser.withUpdatedName(newName);

        assertEquals(userId, updatedUser.id());
        assertEquals(newName, updatedUser.name());
        assertEquals(email, updatedUser.email());
        assertEquals(createdAt, updatedUser.createdAt());
        assertTrue(updatedUser.updatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateEmailWithNewTimestamp() {
        UserId userId = UserId.of("123");
        String name = "John Doe";
        Email originalEmail = Email.of("john.doe@example.com");
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusHours(1);

        User originalUser = new User(userId, name, originalEmail, createdAt, originalUpdatedAt);
        
        Email newEmail = Email.of("jane.doe@example.com");
        User updatedUser = originalUser.withUpdatedEmail(newEmail);

        assertEquals(userId, updatedUser.id());
        assertEquals(name, updatedUser.name());
        assertEquals(newEmail, updatedUser.email());
        assertEquals(createdAt, updatedUser.createdAt());
        assertTrue(updatedUser.updatedAt().isAfter(originalUpdatedAt));
    }
}
