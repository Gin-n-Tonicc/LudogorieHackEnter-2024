package com.ginAndTonic.LudogorieHackEnter2024.exceptions.email;

import com.ginAndTonic.LudogorieHackEnter2024.exceptions.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Exception thrown to indicate that the user's email has not been verified.
 * Extends BadRequestException and sets the appropriate message using MessageSource (the messages are in src/main/resources/messages).
 */
public class EmailNotVerified extends BadRequestException {
    public EmailNotVerified() {
        super("Email must be verified to log in.");
    }
}