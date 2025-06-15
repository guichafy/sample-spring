package guichafy.sample_api.infrastructure.adapters.output;

import guichafy.sample_api.application.ports.input.RouteFilter;
import guichafy.sample_api.application.ports.output.RouteApiPort;
import guichafy.sample_api.domain.entities.Route;
import guichafy.sample_api.domain.valueobjects.RouteId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class ExternalRouteApiAdapter implements RouteApiPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalRouteApiAdapter(
            RestTemplate restTemplate,
            @Value("${app.external-api.base-url:https://api.example.com}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public CompletableFuture<Optional<Route>> findRouteByIdAsync(RouteId routeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/routes/" + routeId.value();
                ExternalRouteResponse response = restTemplate.getForObject(url, ExternalRouteResponse.class);
                
                if (response == null) {
                    return Optional.empty();
                }
                
                return Optional.of(mapToRoute(response));
                
            } catch (Exception e) {
                // Log error and return empty - in production, implement proper error handling
                System.err.println("Error fetching route " + routeId + ": " + e.getMessage());
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<Route>> findAllRoutesAsync(RouteFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/routes");
                
                // Adiciona filtros como query parameters
                if (filter.method() != null) {
                    builder.queryParam("method", filter.method());
                }
                if (filter.tags() != null && !filter.tags().isEmpty()) {
                    builder.queryParam("tags", String.join(",", filter.tags()));
                }
                if (filter.isActive() != null) {
                    builder.queryParam("active", filter.isActive());
                }
                if (filter.pathContains() != null) {
                    builder.queryParam("path", filter.pathContains());
                }
                if (filter.page() != null) {
                    builder.queryParam("page", filter.page());
                }
                if (filter.size() != null) {
                    builder.queryParam("size", filter.size());
                }
                
                String url = builder.toUriString();
                ExternalRouteResponse[] responses = restTemplate.getForObject(url, ExternalRouteResponse[].class);
                
                if (responses == null) {
                    return Collections.emptyList();
                }
                
                return Arrays.stream(responses)
                    .map(this::mapToRoute)
                    .toList();
                    
            } catch (Exception e) {
                // Log error and return empty list - in production, implement proper error handling
                System.err.println("Error fetching routes with filter " + filter + ": " + e.getMessage());
                return Collections.emptyList();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Route>> enrichRouteMetadataAsync(Route route) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/routes/" + route.id().value() + "/metadata";
                Map<String, Object> metadata = restTemplate.getForObject(url, Map.class);
                
                if (metadata == null || metadata.isEmpty()) {
                    return Optional.of(route);
                }
                
                // Combina metadados existentes com os novos
                Map<String, Object> combinedMetadata = new HashMap<>(route.metadata());
                combinedMetadata.putAll(metadata);
                
                Route enrichedRoute = route.withUpdatedMetadata(combinedMetadata);
                return Optional.of(enrichedRoute);
                
            } catch (Exception e) {
                // Log error and return original route - in production, implement proper error handling
                System.err.println("Error enriching route metadata for " + route.id() + ": " + e.getMessage());
                return Optional.of(route);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isRouteActiveAsync(RouteId routeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/routes/" + routeId.value() + "/status";
                ExternalRouteStatusResponse response = restTemplate.getForObject(url, ExternalRouteStatusResponse.class);
                
                return response != null && response.active();
                
            } catch (Exception e) {
                // Log error and return false - in production, implement proper error handling
                System.err.println("Error checking route status for " + routeId + ": " + e.getMessage());
                return false;
            }
        });
    }

    private Route mapToRoute(ExternalRouteResponse response) {
        return new Route(
            RouteId.of(response.id()),
            response.path(),
            response.name(),
            response.description(),
            response.method(),
            response.tags() != null ? response.tags() : Collections.emptyList(),
            response.metadata() != null ? response.metadata() : Collections.emptyMap(),
            response.active(),
            response.createdAt() != null ? response.createdAt() : LocalDateTime.now(),
            response.updatedAt() != null ? response.updatedAt() : LocalDateTime.now()
        );
    }

    // DTOs para comunicação com API externa
    public record ExternalRouteResponse(
        String id,
        String path,
        String name,
        String description,
        String method,
        List<String> tags,
        Map<String, Object> metadata,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record ExternalRouteStatusResponse(
        String id,
        boolean active,
        LocalDateTime lastChecked
    ) {}
}