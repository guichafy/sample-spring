package guichafy.sample_api.application.ports.input;

import guichafy.sample_api.domain.entities.Route;

import java.util.List;

public interface GetRoutesUseCase {
    List<Route> getAllRoutes(RouteFilter filter);
}