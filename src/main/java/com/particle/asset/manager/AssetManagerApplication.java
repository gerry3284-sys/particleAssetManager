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
	// "code" di Movement per casi Dismissed → DICA1199603221 (come l'altro ma senza UserId)
	// Esempio "OID" → 550e8400-e29b-41d4-a716-446655440000 → 32 caratteri esadecimali (0-9 e a-f) →
	// Divisi in 5 gruppi separati da trattini → Formato: 8-4-4-4-12 (36 caratteri totali per via del separatore)
	// TODO: Concludere di Risolvere - scritto il 20/04/2026 da Gerry (Nome Segreto: Geremia)

	// Operazioni da Effettuare
	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK) - TBA
	// TODO: Modificare da "boolean" a "Boolean" (per avere "NULL") ?
	//		 Potrebbe avere senso per far si che serva obbligatoriamente il valore
	// TODO: Modificare il salvataggio del file rendendolo obbligatorio in un'operazione POST del Movement
	// TODO: Se si dovesse togliere Hard Disk e Ram di un Tipo di Asset deve azzerarsi a DB ?
	// TODO: Restituire il nome dello statusCode per "GET user/{code}/movement" ?
	// TODO: Modificare la PUT per lo status dell'asset {OK} - La modifica è temporanea. Da cambiare con gli enum
	// TODO: Aggiungere la cache dove serve
	// TODO: Modificare gli "schema" delle varie ApiResponse {OK} - dare un'altra occhiata per sicurezza
	// TODO: Quando si aggiorna un dato senza cambiare i dati, effettua il salvataggio
	//		 dei dati quando non cambia nulla.
	// TODO: Inserire un commento (obbligatorio da FE) per quando si entra all'interno di un asset in manutenzione
	// TODO: Effettuare un collegamento molti a molti tra Ticket e Manutenzione ?
	// TODO: Inserire i sotto-stati per ticket e manutenzione per una priorità per quando si lavora con essi
	//		 Bassa, Medio, Alta → Il livello di priorità deve diventare maggiore quando passa molto tempo
	//		 dall'ultima interazione, dell'admin da esso
	// TODO: Inserire un attributo per avere lo stato "in lavorazione" per un asset in manutenzione {OK} - Momentaneo
	// TODO: Aggiungere un altro ResponseDto per quando si cambia lo status del asset manutenzione
	// TODO: Mettere la possibilità di "inProgress" di passare da 1 a 0 dopo un tot. di tempo per i Ticket
	// TODO: I ticket reply devono essere gestiti con i socket (facoltativo)
	// TODO: Cancellare il valore di default per priority in Ticket

	// Cambiamenti Effettuati
	// Inserita la priorità nell'aggiornamento dello Status dell'Asset
	// Inserita la priorità nel movimento di Riconsegna
	// Inserito il controllo per il limite dei caratteri quando si apre un ticket o invia una risposta
	// Modificati i valori di base di application-insert-data.sql
	// Inserita la priorità alla creazione del ticket

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
