package guichafy.sample_api.application.ports.input;

import guichafy.sample_api.domain.entities.Todo;

import java.util.List;

public interface GetTodosUseCase {
    List<Todo> getTodos();
}
