package guichafy.sample_api.infrastructure.adapters.input.web;

import guichafy.sample_api.application.ports.input.GetRouteUseCase;
import guichafy.sample_api.application.ports.input.GetRoutesUseCase;
import guichafy.sample_api.application.ports.input.RouteFilter;
import guichafy.sample_api.domain.entities.Route;
import guichafy.sample_api.domain.valueobjects.RouteId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sitemap")
public class SitemapController {

    private final GetRouteUseCase getRouteUseCase;
    private final GetRoutesUseCase getRoutesUseCase;

    public SitemapController(GetRouteUseCase getRouteUseCase, GetRoutesUseCase getRoutesUseCase) {
        this.getRouteUseCase = getRouteUseCase;
        this.getRoutesUseCase = getRoutesUseCase;
    }

    /**
     * Endpoint para buscar uma rota específica por ID
     * GET /api/sitemap/routes/{id}
     */
    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable String id) {
        RouteId routeId = RouteId.of(id);
        Optional<Route> route = getRouteUseCase.getRouteById(routeId);
        
        return route.map(r -> ResponseEntity.ok(RouteResponse.fromRoute(r)))
                   .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para buscar todas as rotas com filtros REST
     * GET /api/sitemap/routes?method=GET&tags=api,public&active=true&path=/api&page=0&size=20
     */
    @GetMapping("/routes")
    public ResponseEntity<List<RouteResponse>> getAllRoutes(
            @RequestParam(required = false) String method,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String path,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        RouteFilter filter = new RouteFilter(method, tags, active, path, page, size);
        List<Route> routes = getRoutesUseCase.getAllRoutes(filter);
        
        List<RouteResponse> responses = routes.stream()
            .map(RouteResponse::fromRoute)
            .toList();
            
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint para buscar rotas ativas apenas
     * GET /api/sitemap/routes/active
     */
    @GetMapping("/routes/active")
    public ResponseEntity<List<RouteResponse>> getActiveRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        RouteFilter filter = new RouteFilter(null, null, true, null, page, size);
        List<Route> routes = getRoutesUseCase.getAllRoutes(filter);
        
        List<RouteResponse> responses = routes.stream()
            .map(RouteResponse::fromRoute)
            .toList();
            
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint para buscar rotas por método HTTP
     * GET /api/sitemap/routes/method/{method}
     */
    @GetMapping("/routes/method/{method}")
    public ResponseEntity<List<RouteResponse>> getRoutesByMethod(
            @PathVariable String method,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        RouteFilter filter = new RouteFilter(method.toUpperCase(), null, null, null, page, size);
        List<Route> routes = getRoutesUseCase.getAllRoutes(filter);
        
        List<RouteResponse> responses = routes.stream()
            .map(RouteResponse::fromRoute)
            .toList();
            
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint para buscar rotas por tags
     * GET /api/sitemap/routes/tags?tags=api,public
     */
    @GetMapping("/routes/tags")
    public ResponseEntity<List<RouteResponse>> getRoutesByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        RouteFilter filter = new RouteFilter(null, tags, null, null, page, size);
        List<Route> routes = getRoutesUseCase.getAllRoutes(filter);
        
        List<RouteResponse> responses = routes.stream()
            .map(RouteResponse::fromRoute)
            .toList();
            
        return ResponseEntity.ok(responses);
    }
}