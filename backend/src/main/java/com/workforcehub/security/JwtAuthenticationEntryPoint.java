package com.workforcehub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforcehub.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT authentication entry point that returns 401 for unauthenticated requests.
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("Unauthorized request to: {}", request.getRequestURI());

        // Check if it's an API request or a page request
        String acceptHeader = request.getHeader("Accept");
        boolean isApiRequest = request.getRequestURI().startsWith("/api/") ||
                (acceptHeader != null && acceptHeader.contains("application/json"));

        if (isApiRequest) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            mapper.writeValue(response.getOutputStream(),
                    ApiResponse.error("Unauthorized: " + authException.getMessage()));
        } else {
            // Redirect to login page for browser requests
            response.sendRedirect("/login");
        }
    }
}
