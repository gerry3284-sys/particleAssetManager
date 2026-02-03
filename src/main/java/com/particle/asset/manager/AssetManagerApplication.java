package com.particle.asset.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.particle.asset.manager")
public class AssetManagerApplication
{
	// http://localhost:8080/swagger-ui/index.html --> Swagger
	// https://www.base64decode.org/ → Sito per verificare la firma dei token
	// Versione JDK: ms 21
	// mvn clean install (su terminale) --> per effettuare i test
	// Business Unit base = {Particle, Value, Ask, Kite}
	// Errori 401 e 403 gestiti in SecurityConfig.java
	// Errore 500 gestito in "GlobalExceptionHandler.java"
	// RefreshTokenService.java --> cancellato

	// TODO: Continuare la gestione degli errori (controllandoli per bene)
	// TODO: Mostrare l'errore corretto per diversi codici d'errore
	// TODO: Tradurre gli errori
	// TODO: Per il nome dell'hard disk usare un enum (inserire più tipi)
	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK)
	// TODO: Cancellare la creazione del token attraverso il database
	// TODO: Modificare le PUT in PATCH
	// TODO: Controllare i vari errori quando ci sono sia i 400 che i 404 (vedere quale errore mostrare prima)
	// TODO: Creare una tabella PDF per le ricevute

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
