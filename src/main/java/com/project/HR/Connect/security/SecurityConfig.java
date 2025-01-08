package com.project.HR.Connect.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private AuthTokenFilter authTokenFilter; // Token filter for JWT authentication

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().
    csrf().disable()
                .authorizeRequests()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Allow access to swagger and api-docs without authentication
                .requestMatchers("/auth/**").permitAll()  // Allow login and registration endpoints from auth-service without JWT
                .anyRequest().authenticated()  // Protect all other routes
                .and()
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class); // Add the JWT filter

        return http.build();
    }
}

