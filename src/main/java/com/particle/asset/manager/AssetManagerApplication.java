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
	// TODO: Mettere un Code anche per Movement {OK}
	// TODO: Modificare la creazione del DB e l'inserimento dei dati per farlo combaciare
	//		 con le modifiche del Movement (aggiunta di Code)
	// TODO: Modificare il salvataggio del file rendendolo obbligatorio in un'operazione POST del Movement
	// TODO: Creare un "Operations" in "enums" anche per "User" ?
	// TODO: Togliere "active" da AssetStatusType {OK}
	// TODO: Se si dovesse togliere Hard Disk e Ram di un Tipo di Asset deve azzerarsi a DB ?
	// TODO: Aggiungere gli errori relativi al salvataggio delle ricevute
	// TODO: Aggiungere il nome della ricevuta alla response 200 dopo aver effettuato
	//		 un'operazione di movimento (è possibile farlo ?)
	// TODO: Modificare il form dell'autorizzazione in Swagger (esteticamente)
	// TODO: Aggiungere (anche se molto probabilmente non sarà mai usato) un errore per l'assenza di file nella
	//		 GET del Movement. {OK}
	// TODO: Inserire un enum per i tre tipi di base dei movement {OK}
	// TODO: Modificare la GET per i movimenti di un utente {OK}
	// TODO: Inserire l'errore per quando c'è un movimento ma il file non è presente {OK}
	// TODO: Dire ad Andrea che "Available", "Assigned" e "Dismissed" non possono essere modificati
	// TODO: Quando viene fatto il Dismissed, l'id dell'utente deve essere quello di un admin.

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
