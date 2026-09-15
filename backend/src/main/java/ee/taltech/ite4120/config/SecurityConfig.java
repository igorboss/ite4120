package ee.taltech.ite4120.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Course-template security: open documentation and infrastructure, mock-authenticated API.
 *
 * <p>{@code /api/**} requires an {@code Authorization: Bearer <username>} header, resolved
 * by {@link MockBearerAuthFilter} when {@code auth.mock.enabled=true} (the local profile).
 * With mock auth disabled there is no authentication provider at all, so every API request
 * is rejected — the template fails CLOSED, the same posture as the production platform.
 *
 * <p>This mirrors, in ~30 lines, what {@code org.helex.emr:commons-mock-auth} +
 * {@code core-backend}'s SecurityConfig do on the real platform. The wire contract is
 * identical, which is why the frontend's {@code MockAuthProvider} works unchanged.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MockBearerAuthFilter mockAuth)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Spring dispatches 404s and exceptions to /error internally;
                        // without this the ERROR dispatch falls into denyAll and every
                        // 404 turns into a 401 — a classic, confusing trap.
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
                        // Open: health, API contract, Swagger console, the mock registry.
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/mock-registry/**").permitAll()
                        // The frontend's MockAuthProvider probes these before it has a
                        // session; the platform permits them pre-auth, and so do we. With
                        // no controller behind them they 404, which the provider treats
                        // as "no uma backend" and falls back to its guest profile.
                        .requestMatchers("/api/uma/auth/**", "/api/uma/userinfo").permitAll()
                        // Everything business-facing requires an authenticated session.
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())
                // Missing/invalid credentials are 401 (who are you?); an authenticated
                // request without rights would be 403 (I know, and no). The course's
                // S3 distinction, enforced here — Spring's default is 403 for both.
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(mockAuth, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
