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
	// "code" di Movement → RE1CA1199603221
	// RE → MovementType (primi 2 caratteri), 1 → UserId, CA1 → AssetCode,
	// 19960322 → MovementDate (yyyymmdd), 1 → Numero Record Tabella

	// TODO: Per il nome dell'hard disk usare un enum (inserire più tipi) - TBA
	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK) - TBA
	// TODO: Cancellare la creazione del token attraverso il database - TBA
	// TODO: Modificare da "boolean" a "Boolean" (per avere "NULL") ?
	// TODO: Modificare il salvataggio del file rendendolo obbligatorio in un'operazione POST del Movement
	// TODO: Creare un "Operations" in "enums" anche per "User" ?
	// TODO: Se si dovesse togliere Hard Disk e Ram di un Tipo di Asset deve azzerarsi a DB ?
	// TODO: Aggiungere gli errori relativi al salvataggio delle ricevute
	// TODO: Aggiungere il nome della ricevuta alla response 200 dopo aver effettuato
	//		 un'operazione di movimento (è possibile farlo ?)
	// TODO: Aggiungere un endpoint differente per la dismissione degli Asset ?
	//		 Questo potrebbe essere fatto per via del controllo che si effettua per mettere
	//		 a null i dati
	// TODO: Far si che un Asset possa essere modificato solo se esso è Available
	// TODO: Far si che la GET user/{id}/movement restituisca il code del movement
	// TODO: Far si che le varie chiamate non restituiscano più l'id perchè inutile
	//		 (fatta eccezione per l'utente)
	// TODO: I movimenti degli asset che hanno null come ID devono essere gestiti affinchè
	//		 non venga generato l'errore 500

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
