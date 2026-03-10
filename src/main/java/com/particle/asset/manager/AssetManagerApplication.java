package com.particle.asset.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.particle.asset.manager")
public class AssetManagerApplication
{
	// http://localhost:8080/swagger-ui/index.html --> Swagger
	// https://www.base64decode.org/ → Sito per verificare la firma dei token JWT (Non serve più)
	// Versione JDK: ms 21
	// mvn clean install (su terminale) --> per effettuare i test
	// Business Unit base = {Particle, Value, Ask, Kite}
	// Errori 401 e 403 gestiti in SecurityConfig.java
	// Errore 500 gestito in "GlobalExceptionHandler.java"
	// Ricevute (PDF) gestite in "Movement" e salvate nella cartella "receipts"
	// I PDF ricevuti devono essere in Base64

	// TODO: Continuare la gestione degli errori (controllandoli per bene)
	// TODO: Mostrare l'errore corretto per diversi codici d'errore
	// TODO: Per il nome dell'hard disk usare un enum (inserire più tipi)
	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK)
	// TODO: Cancellare la creazione del token attraverso il database
	// TODO: Controllare i vari errori quando ci sono sia i 400 che i 404 (vedere quale errore mostrare prima)
	// TODO: Modificare da "boolean" a "Boolean" (per avere "NULL") ?
	// TODO: Modificare la Response dell'attivazione/disattivazione dell'AssetType aggiungendo RAM e Hard Disk ?
	// TODO: Modificare gli errori. SwaggerResponses va bene però è troppo generica.
	//		 Creare delle SwaggerResponses per i le varie entità così da avere messaggi d'errore
	//		 più precisi (Controllare quali possano essere generici
	//		 e quali hanno necessità di una nuova classe) - WIP
	// TODO: Mettere un Code anche per Movement ?

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
