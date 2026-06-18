package com.particle.asset.manager.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.particle.asset.manager.enums.UserTypes;
import com.particle.asset.manager.models.BusinessUnit;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.BusinessUnitRepository;
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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final MicrosoftGraphService microsoftGraphService;
    private final BusinessUnitRepository businessUnitRepository;

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
            Jwt jwt = jwtDecoder.decode(token);

            // [MODIFICA] Inizializzazione valori di default fuori dall'if,
            // per gestire il caso in cui X-Graph-Token non è presente
            BusinessUnit companyName = null;
            String phone = null;
            UserTypes userType = UserTypes.USER;

            // [MODIFICA] Lettura X-Graph-Token — se null, saltiamo Graph
            // e usiamo i valori di default inizializzati sopra
            String graphTokenHeader = request.getHeader("X-Graph-Token");
            if (graphTokenHeader != null) {
                String graphJson = microsoftGraphService.getUserGroups(graphTokenHeader);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(graphJson);
                JsonNode me = root.path("me");
                JsonNode groups = root.path("groups").path("value");

                // [MODIFICA] Controllo null su companyName prima di fare findByName
                // per evitare "where b1_0.name is null"
                String companyNameStr = me.path("companyName").asText(null);
                companyName = companyNameStr != null
                        ? businessUnitRepository.findByName(companyNameStr).orElse(null)
                        : null;

                //String jobTitle = me.path("jobTitle").asText(null);
                phone = me.path("mobilePhone").asText(null);

                for (JsonNode group : groups) {
                    String groupId = group.path("id").asText();
                    if ("22e19954-a4df-4a0a-827f-147cf41299ac".equals(groupId)) {
                        userType = UserTypes.ADMIN;
                        companyName = null;
                        break;
                    }
                }
            }

            final String finalPhone = phone;
            final UserTypes finalUserType = userType;
            final BusinessUnit finalCompanyName = companyName;

            String oid = jwt.getClaimAsString("oid");
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
                        // [INSERIMENTO] Doppio controllo per evitare race condition
                        // quando due request arrivano in parallelo e l'utente non esiste ancora
                        if (userRepository.existsByOid(oid)) {
                            return userRepository.findByOid(oid).orElseThrow();
                        }
                        User newUser = new User();
                        newUser.setOid(oid);
                        newUser.setEmail(finalEmail);
                        String[] nameParts = finalDisplayName.split(" ", 2);
                        newUser.setName(nameParts[0]);
                        newUser.setSurname(nameParts.length > 1 ? nameParts[1] : "");
                        newUser.setPhoneNumber(finalPhone != null ? finalPhone : "");
                        newUser.setUserType(finalUserType);
                        //newUser.setBusinessUnit();
                        newUser.setBusinessUnit(finalCompanyName);
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