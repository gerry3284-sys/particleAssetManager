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
	// mvn clean compile (su terminale) + reload/sync di Maven --> per controllo relativi errori
	// Business Unit base = {Particle, Value, Ask, Kite}
	// Errori 401 e 403 gestiti in SecurityConfig.java
	// Errore 500 gestito in "GlobalExceptionHandler.java"
	// Ricevute (PDF) gestite in "Movement" e salvate nella cartella "receipts"
	// I PDF ricevuti devono essere in Base64

	// TODO: Per il nome dell'hard disk usare un enum (inserire più tipi) - TBA
	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK) - TBA
	// TODO: Cancellare la creazione del token attraverso il database - TBA
	// TODO: Modificare da "boolean" a "Boolean" (per avere "NULL") ?
	// TODO: Modificare la Response dell'attivazione/disattivazione dell'AssetType aggiungendo RAM e Hard Disk ? {OK}
	// TODO: Mettere un Code anche per Movement ?
	// TODO: Creare un "Operations" in "enums" anche per "User" ?
	// TODO: Una BU può essere disattivata solo nel caso non sia collegata a nessun Asset. {OK}
	// TODO: Togliere la disattivazione dello stato degli Asset (non possono essere più disattivati). {OK}
	// TODO: Inserire i messaggi errore d'esempio su Swagger per le modifiche di BusinessUnit. {OK}

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
