package com.example.sistemagestao.infra.security;

import jakarta.servlet.http.HttpServletResponse;
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
                .cors(cors -> cors.configure(httpSecurity))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
                            response.sendError(401, "Unauthorized");
                        })
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/register-client",
                                "/api/initiallize")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/bakery/**",
                                "/api/category/**",
                                "/api/product/get/*",
                                "/api/product/search-active",
                                "/api/order/order-in-cart/*",
                                "/api/order/all-by-user/*",
                                "/api/statistics/orders-user/*"
                        )
                        .hasRole("CLIENT")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/order/make",
                                "/api/order/cancel/*"
                        )
                        .hasRole("CLIENT")

                        .requestMatchers(
                                "/api/order/add-product",
                                "/api/order/remove-product",
                                "/api/product-review/**"
                                )
                        .hasRole("CLIENT")

                        .requestMatchers(
                                "/api/order/set-order-ready/*",
                                "/api/order/set-order-delivered/*",
                                "/api/order/search-username-day/*",
                                "/api/order/get-accepted-by-date/"
                        )
                        .hasRole("COUNTER_EMPLOYEE")

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
                                "/api/user/**",
                                "/api/order/**",
                                "/api/statistics/**"
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
