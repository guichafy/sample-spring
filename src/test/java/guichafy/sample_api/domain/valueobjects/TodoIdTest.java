package guichafy.sample_api.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoIdTest {

    @Test
    void shouldCreateValidTodoId() {
        String value = "123";
        TodoId todoId = TodoId.of(value);

        assertEquals(value, todoId.value());
        assertEquals(value, todoId.toString());
    }

    @Test
    void shouldCreateTodoIdWithNumericString() {
        String value = "456";
        TodoId todoId = new TodoId(value);

        assertEquals(value, todoId.value());
    }

    @Test
    void shouldCreateTodoIdWithAlphanumericString() {
        String value = "abc123";
        TodoId todoId = TodoId.of(value);

        assertEquals(value, todoId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TodoId(null)
        );

        assertEquals("TodoId cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TodoId.of("")
        );

        assertEquals("TodoId cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TodoId("   ")
        );

        assertEquals("TodoId cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        TodoId todoId1 = TodoId.of("123");
        TodoId todoId2 = TodoId.of("123");

        assertEquals(todoId1, todoId2);
        assertEquals(todoId1.hashCode(), todoId2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        TodoId todoId1 = TodoId.of("123");
        TodoId todoId2 = TodoId.of("456");

        assertNotEquals(todoId1, todoId2);
    }

    @Test
    void shouldReturnValueAsString() {
        String value = "test-todo-id";
        TodoId todoId = TodoId.of(value);

        assertEquals(value, todoId.toString());
    }
}
