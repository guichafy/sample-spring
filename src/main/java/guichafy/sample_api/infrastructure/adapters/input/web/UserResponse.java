package guichafy.sample_api.infrastructure.adapters.input.web;

import guichafy.sample_api.domain.entities.User;

import java.time.LocalDateTime;

public record UserResponse(
    String id,
    String name,
    String email,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.id().value(),
            user.name(),
            user.email().value(),
            user.createdAt(),
            user.updatedAt()
        );
    }
}