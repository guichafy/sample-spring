package guichafy.sample_api.modules.todos.infrastructure.representation;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import com.fasterxml.jackson.annotation.JsonInclude;

@Relation(collectionRelation = "todos", itemRelation = "todo")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoResponse extends RepresentationModel<TodoResponse> {
    private Long userId;
    private Long id;
    private String title;
    private boolean completed;

    // Construtores, Getters e Setters
    public TodoResponse() {}

    public TodoResponse(Long userId, Long id, String title, boolean completed) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}