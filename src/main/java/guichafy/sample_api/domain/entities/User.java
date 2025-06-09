package guichafy.sample_api.domain.entities;

import guichafy.sample_api.domain.valueobjects.Email;
import guichafy.sample_api.domain.valueobjects.UserId;

import java.time.LocalDateTime;

public record User(
    UserId id,
    String name,
    Email email,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public User {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }

    public User withUpdatedName(String newName) {
        return new User(id, newName, email, createdAt, LocalDateTime.now());
    }

    public User withUpdatedEmail(Email newEmail) {
        return new User(id, name, newEmail, createdAt, LocalDateTime.now());
    }
}