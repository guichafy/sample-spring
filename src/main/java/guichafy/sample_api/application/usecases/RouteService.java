package guichafy.sample_api.application.usecases;

import guichafy.sample_api.application.ports.input.GetRouteUseCase;
import guichafy.sample_api.application.ports.input.GetRoutesUseCase;
import guichafy.sample_api.application.ports.input.RouteFilter;
import guichafy.sample_api.application.ports.output.RouteApiPort;
import guichafy.sample_api.domain.entities.Route;
import guichafy.sample_api.domain.valueobjects.RouteId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class RouteService implements GetRouteUseCase, GetRoutesUseCase {

    private final RouteApiPort routeApiPort;

    public RouteService(RouteApiPort routeApiPort) {
        this.routeApiPort = routeApiPort;
    }

    @Override
    public Optional<Route> getRouteById(RouteId routeId) {
        try {
            // Busca a rota básica
            CompletableFuture<Optional<Route>> routeFuture = routeApiPort.findRouteByIdAsync(routeId);
            
            // Verifica se está ativa em paralelo
            CompletableFuture<Boolean> activeFuture = routeApiPort.isRouteActiveAsync(routeId);
            
            // Aguarda ambas as operações
            Optional<Route> routeOpt = routeFuture.get();
            Boolean isActive = activeFuture.get();
            
            if (routeOpt.isEmpty()) {
                return Optional.empty();
            }
            
            Route route = routeOpt.get();
            
            // Se o status de ativo for diferente, atualiza
            if (route.isActive() != isActive) {
                route = route.withActiveStatus(isActive);
            }
            
            // Enriquece com metadados adicionais de forma assíncrona
            CompletableFuture<Optional<Route>> enrichedFuture = routeApiPort.enrichRouteMetadataAsync(route);
            Optional<Route> enrichedRoute = enrichedFuture.get();
            
            return enrichedRoute.isPresent() ? enrichedRoute : Optional.of(route);
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching route: " + routeId, e);
        }
    }

    @Override
    public List<Route> getAllRoutes(RouteFilter filter) {
        try {
            // Busca todas as rotas com filtros de forma assíncrona
            CompletableFuture<List<Route>> routesFuture = routeApiPort.findAllRoutesAsync(filter);
            List<Route> routes = routesFuture.get();
            
            // Para cada rota, verifica status ativo em paralelo usando Virtual Threads
            List<CompletableFuture<Route>> enrichmentFutures = routes.stream()
                .map(route -> 
                    routeApiPort.isRouteActiveAsync(route.id())
                        .thenApply(isActive -> {
                            if (route.isActive() != isActive) {
                                return route.withActiveStatus(isActive);
                            }
                            return route;
                        })
                )
                .toList();
            
            // Aguarda todas as verificações de status
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                enrichmentFutures.toArray(new CompletableFuture[0])
            );
            
            allFutures.get();
            
            // Coleta os resultados
            return enrichmentFutures.stream()
                .map(CompletableFuture::join)
                .toList();
                
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching routes with filter: " + filter, e);
        }
    }
}