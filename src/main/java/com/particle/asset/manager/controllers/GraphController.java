package com.particle.asset.manager.controllers;

import com.particle.asset.manager.services.MicrosoftGraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/graph")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class GraphController {

    private final MicrosoftGraphService microsoftGraphService;

    /*@PostMapping("/groups")
    public String getUserGroups(@RequestHeader("Authorization") String bearerToken) throws Exception {
        String token = bearerToken.substring(7);
        return microsoftGraphService.getUserGroups(token);
    }*/

    @PostMapping("/groups")
    public ResponseEntity<String> getGroups(
            @RequestHeader("X-Graph-Token") String graphToken) throws Exception {
        return ResponseEntity.ok(microsoftGraphService.getUserGroups(graphToken));
    }
}
