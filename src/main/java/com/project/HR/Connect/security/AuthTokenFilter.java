package com.project.HR.Connect.security;

import com.auth0.jwt.JWT;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain)
            throws ServletException, IOException {
        try {
            assert request != null;
            String jwt = parseJwt(request); // Parse JWT from the Authorization header
            if (jwt != null && !jwt.isBlank() && jwtUtils.validateJwtToken(jwt)) {
                // Extract the email (username) from the JWT token
                String email = jwtUtils.getEmailFromJwtToken(jwt);

                // Here we can extract authorities from the JWT token if available
                // Assuming "role" claim is in the JWT, you can also add other claims as authorities
                String role = JWT.decode(jwt).getClaim("role").asString(); // Extract role from JWT
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);

                // Build the authentication object directly from the extracted information
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email, // The username (email) extracted from the JWT
                                null,  // We don't need the password in this case
                                authorities // Add authorities (roles) from the token
                        );

                // Set the authentication details in the SecurityContext
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        assert filterChain != null;
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}


