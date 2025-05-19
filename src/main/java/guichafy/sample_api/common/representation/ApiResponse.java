package guichafy.sample_api.common.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> extends RepresentationModel<ApiResponse<T>> {

    private T data;

    public ApiResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Necessário para desserialização do Jackson se usado em requests, e para construtores vazios
    public ApiResponse() {
    }
}