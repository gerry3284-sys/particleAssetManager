package com.particle.asset.manager.security;

import com.particle.asset.manager.enums.UserTypes;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.UserRepository;
import com.particle.asset.manager.services.MicrosoftGraphService;
import com.particle.asset.manager.swaggerResponses.GenericResponses;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final MicrosoftGraphService microsoftGraphService;

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
        System.out.println("TOKEN: " + token);
        try {
            Jwt jwt = jwtDecoder.decode(token);


            //String groupsResponse = microsoftGraphService.getUserGroups(token);
            //System.out.println("GROUP: " + groupsResponse);

            // DEBUG
            jwt.getClaims().forEach((key, value) -> {
                System.out.println(key + " = " + value);
            });

            String oid = jwt.getClaimAsString("oid");

            // Token v1.0 usa upn/unique_name, token v2.0 usa preferred_username
            String email = jwt.getClaimAsString("preferred_username");
            if (email == null) email = jwt.getClaimAsString("upn");
            if (email == null) email = jwt.getClaimAsString("unique_name");
            if (email == null) email = oid + "@unknown.com";

            String displayName = jwt.getClaimAsString("name");
            if (displayName == null) displayName = email;

            // Variabili final per uso nella lambda
            final String finalEmail = email;
            final String finalDisplayName = displayName;

            // Auto-provisioning: crea l'utente se non esiste
            User user = userRepository.findByOid(oid)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setOid(oid);
                        newUser.setEmail(finalEmail);
                        String[] nameParts = finalDisplayName.split(" ", 2);
                        newUser.setPhoneNumber("");
                        newUser.setUserType(UserTypes.USER);
                        //newUser.setBusinessUnit();
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

        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE);
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        chain.doFilter(request, response);
    }
}