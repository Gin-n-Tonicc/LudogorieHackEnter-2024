package com.ginAndTonic.LudogorieHackEnter2024.exceptions.user;

import com.ginAndTonic.LudogorieHackEnter2024.exceptions.BadRequestException;
import jakarta.validation.ConstraintViolation;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception thrown when there is an issue creating a user, either due to invalid data or duplicate user details.
 * Sets the appropriate message using MessageSource (the messages are in src/main/resources/messages).
 */
public class UserCreateException extends BadRequestException {

    /**
     * Constructs a UserCreateException with a message indicating either a duplicate email or invalid user data.
     */
    public UserCreateException(MessageSource messageSource, boolean isUnique) {
        super(
                isUnique
                        ? "An user already exists with the same email!"
                        : "Invalid user data!"
        );
    }

    /**
     * Constructs a UserCreateException with validation errors.
     */
    public UserCreateException(Set<ConstraintViolation<?>> validationErrors) {
        super(
                validationErrors
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("\n"))
        );
    }
}
