package com.particle.asset.manager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MicrosoftGraphService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getUserGroups(String graphToken) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // Chiamata 1: dati utente
        HttpResponse<String> meResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("https://graph.microsoft.com/v1.0/me?$select=mail,givenName,surname,businessPhones,department,companyName,jobTitle,officeLocation,mobilePhone,employeeId,displayName"))
                        .header("Authorization", "Bearer " + graphToken)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Chiamata 2: gruppi
        HttpResponse<String> groupsResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("https://graph.microsoft.com/v1.0/me/memberOf"))
                        .header("Authorization", "Bearer " + graphToken)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Unisci le due response in un unico oggetto JSON
        ObjectNode result = objectMapper.createObjectNode();
        result.set("me", objectMapper.readTree(meResponse.body()));
        result.set("groups", objectMapper.readTree(groupsResponse.body()));

        return objectMapper.writeValueAsString(result);
    }
}