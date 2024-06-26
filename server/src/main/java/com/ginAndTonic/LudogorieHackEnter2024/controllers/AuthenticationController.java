package com.ginAndTonic.LudogorieHackEnter2024.controllers;

import com.ginAndTonic.LudogorieHackEnter2024.config.FrontendConfig;
import com.ginAndTonic.LudogorieHackEnter2024.exceptions.email.EmailNotVerified;
import com.ginAndTonic.LudogorieHackEnter2024.exceptions.user.UserNotFoundException;
import com.ginAndTonic.LudogorieHackEnter2024.filters.JwtAuthenticationFilter;
import com.ginAndTonic.LudogorieHackEnter2024.model.dto.auth.AuthenticationRequest;
import com.ginAndTonic.LudogorieHackEnter2024.model.dto.auth.AuthenticationResponse;
import com.ginAndTonic.LudogorieHackEnter2024.model.dto.auth.PublicUserDTO;
import com.ginAndTonic.LudogorieHackEnter2024.model.dto.auth.RegisterRequest;
import com.ginAndTonic.LudogorieHackEnter2024.model.dto.request.CompleteOAuthRequest;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.User;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.VerificationToken;
import com.ginAndTonic.LudogorieHackEnter2024.repositories.UserRepository;
import com.ginAndTonic.LudogorieHackEnter2024.repositories.VerificationTokenRepository;
import com.ginAndTonic.LudogorieHackEnter2024.services.AuthenticationService;
import com.ginAndTonic.LudogorieHackEnter2024.services.impl.event.OnPasswordResetRequestEvent;
import com.ginAndTonic.LudogorieHackEnter2024.services.impl.event.OnRegistrationCompleteEvent;
import com.ginAndTonic.LudogorieHackEnter2024.util.CookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;

import static com.ginAndTonic.LudogorieHackEnter2024.services.impl.security.TokenServiceImpl.AUTH_COOKIE_KEY_JWT;
import static com.ginAndTonic.LudogorieHackEnter2024.services.impl.security.TokenServiceImpl.AUTH_COOKIE_KEY_REFRESH;


/**
 * Controller class for handling authentication-related operations.
 * JWT (access and refresh token);
 * OAuth2;
 * Email confirmation;
 * Forgotten password.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MessageSource messageSource;
    private final FrontendConfig frontendConfig;

    @Value("${server.backend.baseUrl}")
    private String appBaseUrl;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.register(request);
        sendVerificationEmail(modelMapper.map(authenticationResponse.getUser(), User.class));
        return ResponseEntity.ok(authenticationResponse);
    }

    //Endpoint for email confirmation during registration
    @GetMapping("/registrationConfirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token, HttpServletResponse httpServletResponse) throws IOException {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired!");
        }
        verificationToken.setCreatedAt(LocalDateTime.now());

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired!");
        }

        user.setEnabled(true);
        userRepository.save(user);
        httpServletResponse.sendRedirect(frontendConfig.getLoginUrl());
        return ResponseEntity.ok("User registration confirmed successfully!");
    }

    @PostMapping("/authenticate") // login
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse servletResponse) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isEnabled()) {
            throw new EmailNotVerified();
        }

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        authenticationService.attachAuthCookies(authenticationResponse, servletResponse::addCookie);

        return ResponseEntity.ok(authenticationResponse);
    }

    @PutMapping("/complete-oauth")
    // After registering with Google we need more information about the user, described in CompleteOAuthRequest
    public ResponseEntity<AuthenticationResponse> completeOAuth(@RequestBody CompleteOAuthRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        PublicUserDTO currentLoggedUser = (PublicUserDTO) servletRequest.getAttribute(JwtAuthenticationFilter.userKey);

        AuthenticationResponse authenticationResponse = authenticationService.completeOAuth2(request, currentLoggedUser);
        authenticationService.attachAuthCookies(authenticationResponse, servletResponse::addCookie);

        return ResponseEntity.ok(authenticationResponse);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = CookieHelper.readCookie(AUTH_COOKIE_KEY_REFRESH, request.getCookies()).orElse(null);

        AuthenticationResponse authenticationResponse = authenticationService.refreshToken(refreshToken);
        authenticationService.attachAuthCookies(authenticationResponse, response::addCookie);

        return ResponseEntity.ok(authenticationResponse);
    }

    @GetMapping("/me") // Retrieves current user information.
    public ResponseEntity<AuthenticationResponse> getMe(HttpServletRequest request, HttpServletResponse response) {
        String jwtToken = CookieHelper.readCookie(AUTH_COOKIE_KEY_JWT, request.getCookies()).orElse(null);

        AuthenticationResponse authenticationResponse = authenticationService.me(jwtToken);
        authenticationService.attachAuthCookies(authenticationResponse, response::addCookie);

        return ResponseEntity.ok(authenticationResponse);
    }

    private void sendVerificationEmail(User user) {
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appBaseUrl));
    }

    @PostMapping("/forgot-password") // Sends link to email so the user can change their password
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        eventPublisher.publishEvent(new OnPasswordResetRequestEvent(user, appBaseUrl));
        return ResponseEntity.ok("Password reset link sent to your email!");
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        authenticationService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }
}
