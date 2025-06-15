package guichafy.sample_api.application.ports.input;

import guichafy.sample_api.domain.entities.Route;
import guichafy.sample_api.domain.valueobjects.RouteId;

import java.util.Optional;

public interface GetRouteUseCase {
    Optional<Route> getRouteById(RouteId routeId);
}