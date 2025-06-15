package guichafy.sample_api.application.ports.input;

import java.util.List;

public record RouteFilter(
    String method,
    List<String> tags,
    Boolean isActive,
    String pathContains,
    Integer page,
    Integer size
) {
    public RouteFilter {
        if (page != null && page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size != null && size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
    }

    public static RouteFilter empty() {
        return new RouteFilter(null, null, null, null, 0, 20);
    }

    public static RouteFilter withPagination(int page, int size) {
        return new RouteFilter(null, null, null, null, page, size);
    }

    public RouteFilter withMethod(String method) {
        return new RouteFilter(method, tags, isActive, pathContains, page, size);
    }

    public RouteFilter withTags(List<String> tags) {
        return new RouteFilter(method, tags, isActive, pathContains, page, size);
    }

    public RouteFilter withActiveStatus(Boolean isActive) {
        return new RouteFilter(method, tags, isActive, pathContains, page, size);
    }

    public RouteFilter withPathContains(String pathContains) {
        return new RouteFilter(method, tags, isActive, pathContains, page, size);
    }
}