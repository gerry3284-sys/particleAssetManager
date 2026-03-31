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
	// I PDF ricevuti devono essere in Base64 (https://www.base64encode.net/pdf-to-base64)

	// TODO: Per il nome dell'hard disk usare un enum (inserire più tipi) - TBA
	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK) - TBA
	// TODO: Cancellare la creazione del token attraverso il database - TBA
	// TODO: Modificare da "boolean" a "Boolean" (per avere "NULL") ?
	// TODO: Mettere un Code anche per Movement ?
	// TODO: Creare un "Operations" in "enums" anche per "User" ?
	// TODO: Togliere "active" da AssetStatusType ?
	// TODO: Se si dovesse togliere Hard Disk e Ram di un Tipo di Asset deve azzerarsi a DB ?
	// TODO: Aggiungere gli errori relativi al salvataggio delle ricevute
	// TODO: Rimettere le Ricevute a NOT NULL
	// TODO: Aggiungere il nome della ricevuta alla response 200 dopo aver effettuato
	//		 un'operazione di movimento
	// TODO: Effettuare un controllo per cancellare i file che non possiedono più un record sul Database
	// TODO: Modificare la response del GET della ricevuta {OK}
	// TODO: Quando si effettua un movimento, se si scrive in minuscolo, il codice viene salvato in
	//		 quella maniera. Deve sempre essere in maiuscolo. {OK}
	// TODO: Modificare il Background Color dello Swagger. {OK}

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
