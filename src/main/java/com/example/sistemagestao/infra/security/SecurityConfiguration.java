package com.example.sistemagestao.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/register-client")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/bakery/**",
                                "/api/category/**",
                                "/api/product/get/*",
                                "/api/product/search-active"
                        )
                        .hasRole("CLIENT")

                        //.requestMatchers()
                        //.hasRole("COUNTER_EMPLOYEE")

                        .requestMatchers(HttpMethod.GET,
                                "/api/recipe/**",
                                "/api/stock/**"
                        )
                        .hasRole("CONFECTIONER")

                        .requestMatchers(
                                "/api/produced-recipe/**"
                        )
                        .hasRole("CONFECTIONER")

                        .requestMatchers(
                                "/api/bakery/**",
                                "/api/category/**",
                                "/api/ingredient/**",
                                "/api/product/**",
                                "/api/recipe/**",
                                "/api/stock/**",
                                "api/user/**"
                        )
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
