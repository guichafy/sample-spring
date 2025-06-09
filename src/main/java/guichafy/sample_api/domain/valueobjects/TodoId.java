package guichafy.sample_api.domain.valueobjects;

public record TodoId(String value) {
    public TodoId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TodoId cannot be null or empty");
        }
    }

    public static TodoId of(String value) {
        return new TodoId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
