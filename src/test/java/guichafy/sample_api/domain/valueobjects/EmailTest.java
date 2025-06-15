package guichafy.sample_api.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        String emailValue = "test@example.com";
        Email email = Email.of(emailValue);

        assertEquals(emailValue, email.value());
        assertEquals(emailValue, email.toString());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        String upperCaseEmail = "TEST@EXAMPLE.COM";
        Email email = new Email(upperCaseEmail);

        assertEquals("test@example.com", email.value());
    }

    @Test
    void shouldAcceptValidEmailFormats() {
        String[] validEmails = {
            "user@domain.com",
            "user.name@domain.com",
            "user+tag@domain.com",
            "user_name@domain.co.uk",
            "123@domain.com",
            "user@sub.domain.com",
            "a@b.co"
        };

        for (String emailValue : validEmails) {
            assertDoesNotThrow(() -> Email.of(emailValue), 
                "Should accept valid email: " + emailValue);
        }
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email(null)
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.of("")
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email("   ")
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidEmailFormats() {
        String[] invalidEmails = {
            "invalid-email",
            "@domain.com",
            "user@",
            "user@domain",
            "user.domain.com",
            "user@domain.",
            "user@@domain.com",
            "user@domain..com",
            "user name@domain.com",
            "user@domain .com"
        };

        for (String invalidEmail : invalidEmails) {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Email.of(invalidEmail),
                "Should reject invalid email: " + invalidEmail
            );
            assertEquals("Invalid email format", exception.getMessage());
        }
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("test@example.com");

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void shouldBeEqualWhenCasesDiffer() {
        Email email1 = Email.of("TEST@EXAMPLE.COM");
        Email email2 = Email.of("test@example.com");

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        Email email1 = Email.of("test1@example.com");
        Email email2 = Email.of("test2@example.com");

        assertNotEquals(email1, email2);
    }

    @Test
    void shouldReturnValueAsString() {
        String emailValue = "user@domain.com";
        Email email = Email.of(emailValue);

        assertEquals(emailValue, email.toString());
    }
}
