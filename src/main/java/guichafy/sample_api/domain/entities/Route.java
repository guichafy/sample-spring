package guichafy.sample_api.domain.entities;

import guichafy.sample_api.domain.valueobjects.RouteId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record Route(
    RouteId id,
    String path,
    String name,
    String description,
    String method,
    List<String> tags,
    Map<String, Object> metadata,
    boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public Route {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
    }

    public Route withUpdatedMetadata(Map<String, Object> newMetadata) {
        return new Route(id, path, name, description, method, tags, newMetadata, isActive, createdAt, LocalDateTime.now());
    }

    public Route withActiveStatus(boolean active) {
        return new Route(id, path, name, description, method, tags, metadata, active, createdAt, LocalDateTime.now());
    }

    public Route withUpdatedDescription(String newDescription) {
        return new Route(id, path, name, newDescription, method, tags, metadata, isActive, createdAt, LocalDateTime.now());
    }
}