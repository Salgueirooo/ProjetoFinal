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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

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
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("Token  inválido ou ausente.");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("Sem permissão para aceder a este recurso");
                        })
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/register-client",
                                "/api/initialize/**",
                                "/uploads/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/bakery/**",
                                "/api/category/**",
                                "/api/product/get/*",
                                "/api/product/search-active",
                                "/api/order/order-in-cart/*",
                                "/api/order/all-by-user/*",
                                "/api/order/search-day-by-user/*",
                                "/api/statistics/orders-user/*",
                                "/api/user/get-username"
                        )
                        .hasRole("CLIENT")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/order/make",
                                "/api/order/cancel/*",
                                "/api/order/update-product"
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

                        .requestMatchers("/ws/**").permitAll()

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
