package com.ka2k.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        return converter;
    }
    @Bean
    public SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) {
        http.csrf(c->c.disable())
                .authorizeExchange(ex->ex.pathMatchers("eureka/**").permitAll()
                .anyExchange()
                .authenticated())
                .oauth2ResourceServer(spec -> spec.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
