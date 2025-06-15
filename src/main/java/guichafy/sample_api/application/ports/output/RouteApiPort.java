package guichafy.sample_api.application.ports.output;

import guichafy.sample_api.application.ports.input.RouteFilter;
import guichafy.sample_api.domain.entities.Route;
import guichafy.sample_api.domain.valueobjects.RouteId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RouteApiPort {
    
    /**
     * Busca uma rota específica por ID de forma assíncrona usando Virtual Threads
     */
    CompletableFuture<Optional<Route>> findRouteByIdAsync(RouteId routeId);
    
    /**
     * Busca todas as rotas com filtros de forma assíncrona usando Virtual Threads
     */
    CompletableFuture<List<Route>> findAllRoutesAsync(RouteFilter filter);
    
    /**
     * Busca metadados adicionais de uma rota de forma assíncrona
     */
    CompletableFuture<Optional<Route>> enrichRouteMetadataAsync(Route route);
    
    /**
     * Verifica se uma rota está ativa no sistema externo
     */
    CompletableFuture<Boolean> isRouteActiveAsync(RouteId routeId);
}