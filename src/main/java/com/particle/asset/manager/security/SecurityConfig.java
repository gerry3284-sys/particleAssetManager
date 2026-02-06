package com.particle.asset.manager.security;

import com.particle.asset.manager.swaggerResponses.SwaggerResponses;
import com.particle.asset.manager.services.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoint di autenticazione - PUBBLICI
                        .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/asset/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/assetType/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/businessUnit/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/assetStatusType/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/user/**")).permitAll()


                        // Console H2 - SOLO ADMIN (con AntPathRequestMatcher esplicito)
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                        // Swagger - SOLO ADMIN
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()

                        // Proteggi le PUT
                        /*.requestMatchers(new AntPathRequestMatcher("/asset/**", HttpMethod.PUT.name())).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/assetType/**", HttpMethod.PUT.name())).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/businessUnit/**", HttpMethod.PUT.name())).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/assetStatusType/**", HttpMethod.PUT.name())).hasRole("ADMIN")

                        // Proteggi le POST
                        .requestMatchers(new AntPathRequestMatcher("/asset/**", HttpMethod.POST.name())).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/assetType", HttpMethod.POST.name())).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/businessUnit", HttpMethod.POST.name())).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/assetStatusType", HttpMethod.POST.name())).hasRole("ADMIN")*/

                        // GET di /assetType - tutti gli utenti autenticati
                        //.requestMatchers(new AntPathRequestMatcher("/assetType/**", HttpMethod.GET.name())).authenticated()

                        // Tutte le altre richiedono autenticazione
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE);
                        }))
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(SwaggerResponses.FORBIDDEN_ACCESS_EXAMPLE);
                        })))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}