package guichafy.sample_api.domain.valueobjects;

import java.util.UUID;

public record RouteId(String value) {
    public RouteId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("RouteId cannot be null or empty");
        }
    }

    public static RouteId of(String value) {
        return new RouteId(value);
    }

    public static RouteId generate() {
        return new RouteId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}