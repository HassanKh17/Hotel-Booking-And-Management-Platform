package com.hotel.management.system.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hotel.management.system.model.User;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request,
        HttpServletRequest httpRequest) {
        
        // Compose a Spring Security object of the username and password
        UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(
            request.username(), request.password());

        // Authenticate the username and password
        Authentication auth = authenticationManager.authenticate(token);

        // Register the authenticated user in the security context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Create a new HTTP session, generating the JSESSIONID cookie
        HttpSession session = httpRequest.getSession(true);

        /* Persist the security context into the session so future requests stay
        authenticated */
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext());
        
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User user))
            // Respond with 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String role = user.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(new LoginResponse(user.getUsername(),
            user.getUserId(), role));
    }
    
    @GetMapping("/me")
    public ResponseEntity<Object> getMe(Authentication auth) {
        if (auth == null)
            // Respond with 401 Unauthorized if the user is not authenticated
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Object principal = auth.getPrincipal();
        if (!(principal instanceof User user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String role = user.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(new LoginResponse(user.getUsername(),
            user.getUserId(), role));
    }
}
