package com.particle.asset.manager.security;

import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.UserRepository;
import com.particle.asset.manager.services.MsalTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MsalTokenValidator tokenValidator;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Valida e decodifica il token Microsoft
            DecodedToken decoded = tokenValidator.validateAndDecode(token);

            // Controlla se l'utente esiste, altrimenti lo salva
            User user = userRepository.findByOid(decoded.oid())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setOid(decoded.oid());
                        newUser.setEmail(decoded.email());
                        // Split del nome completo
                        String[] nameParts = decoded.displayName().split(" ", 2);
                        newUser.setName(nameParts[0]);
                        newUser.setSurname(nameParts.length > 1 ? nameParts[1] : "");
                        return userRepository.save(newUser);
                    });

            // Setta l'autenticazione nel contesto Spring Security
            CustomUserDetails userDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token non valido\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}