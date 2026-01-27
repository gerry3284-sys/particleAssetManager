package com.particle.asset.manager.configuration;

//import com.particle.asset.manager.models.RefreshToken;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.UserRepository;
import com.particle.asset.manager.services.JwtService;
//import com.particle.asset.manager.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenGenerator implements CommandLineRunner {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    //private final RefreshTokenService refreshTokenService;

    @Override
    public void run(String... args) throws Exception {
        // Genera token per ADMIN
        String adminEmail = "leon.kennedy@example.it";
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        String adminAccessToken = jwtService.generateAccessToken(adminEmail, admin.getUserType().name());
        //RefreshToken adminRefreshToken = refreshTokenService.createRefreshToken(admin);

        // Genera token per USER
        String userEmail = "luca.bianchi@example.com";
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userAccessToken = jwtService.generateAccessToken(userEmail, user.getUserType().name());
        //RefreshToken userRefreshToken = refreshTokenService.createRefreshToken(user);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔑 TOKENS PER TESTING:");
        System.out.println("=".repeat(80));

        System.out.println("\n👑 ADMIN (" + adminEmail + "):");
        System.out.println("Access Token (60 min): " + adminAccessToken);
        //System.out.println("Refresh Token (1 ora): " + adminRefreshToken.getToken());

        System.out.println("\n👤 USER (" + userEmail + "):");
        System.out.println("Access Token (60 min): " + userAccessToken);
        //System.out.println("Refresh Token (1 ora): " + userRefreshToken.getToken());

        System.out.println("\n" + "=".repeat(80) + "\n");
    }
}