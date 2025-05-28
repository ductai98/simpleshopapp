package com.taild.simpleshopapp.configurations;

import com.taild.simpleshopapp.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity(debug = true)
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    //healthcheck
                                    String.format("%s/healthcheck/*", apiPrefix),
                                    //swagger
                                    //"/v3/api-docs",
                                    //"/v3/api-docs/**",
                                    "/api-docs",
                                    "/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/*",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/swagger-ui/*",
                                    "/swagger-ui/index.html",
                                    //Google login
                                    "users/auth/social-login",
                                    "users/auth/social/callback"

                            )
                            .permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/roles/*", apiPrefix)).hasRole("ADMIN")

                            .requestMatchers(GET,
                                    String.format("%s/policies/*", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/categories/*", apiPrefix)).hasAnyRole("USER", "ADMIN")

                            .requestMatchers(GET,
                                    String.format("%s/products/*", apiPrefix)).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(GET,
                                    String.format("%s/products/images/*", apiPrefix)).hasAnyRole("USER", "ADMIN")

                            .requestMatchers(GET,
                                    String.format("%s/orders/*", apiPrefix)).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(GET,
                                    String.format("%s/users/profile-images/*", apiPrefix)).hasAnyRole("USER", "ADMIN")

                            .requestMatchers(GET,
                                    String.format("%s/order_details/*", apiPrefix)).hasAnyRole("USER", "ADMIN")

                            .anyRequest()
                            .authenticated();
                }).csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
