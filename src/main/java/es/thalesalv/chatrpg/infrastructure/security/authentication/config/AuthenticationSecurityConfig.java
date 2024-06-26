package es.thalesalv.chatrpg.infrastructure.security.authentication.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

import es.thalesalv.chatrpg.infrastructure.security.authentication.filter.DiscordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class AuthenticationSecurityConfig {

    private final DiscordAuthenticationFilter discordRequestFilter;

    @Value("${chatrpg.security.ignored-paths}")
    private String[] ignoredPaths;

    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {

        // TODO verify csrf and cors, they shouldn't be disabled
        return http.addFilterBefore(discordRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges.pathMatchers(ignoredPaths).permitAll())
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .csrf(CsrfSpec::disable)
                .cors(CorsSpec::disable)
                .build();
    }
}
