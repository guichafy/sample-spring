package guichafy.sample_api.infrastructure.adapters.input.web;

import guichafy.sample_api.domain.entities.Route;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record RouteResponse(
    String id,
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
    public static RouteResponse fromRoute(Route route) {
        return new RouteResponse(
            route.id().value(),
            route.path(),
            route.name(),
            route.description(),
            route.method(),
            route.tags(),
            route.metadata(),
            route.isActive(),
            route.createdAt(),
            route.updatedAt()
        );
    }
}