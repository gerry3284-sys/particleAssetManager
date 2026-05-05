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

	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK) - TBA
	// TODO: Modificare da "boolean" a "Boolean" (per avere "NULL") ?
	//		 Potrebbe avere senso per far si che serva obbligatoriamente il valore
	// TODO: Modificare il salvataggio del file rendendolo obbligatorio in un'operazione POST del Movement
	// TODO: Creare un "Operations" in "enums" anche per "User" ?
	// TODO: Se si dovesse togliere Hard Disk e Ram di un Tipo di Asset deve azzerarsi a DB ?
	// TODO: Restituire il nome dello statusCode per "GET user/{code}/movement" ?
	// TODO: Modificare la PUT per lo status dell'asset {OK} - La modifica è temporanea. Da cambiare con gli enum
	// TODO: Aggiungere la cache dove serve
	// TODO: Modificare gli "schema" delle varie ApiResponse {OK} - dare un'altra occhiata per sicurezza
	// TODO: Quando si aggiorna un dato senza cambiare i dati, serve effettuare il salvataggio
	//		 dei dati quando non cambia nulla.
	// TODO: Ha senso tenere il message alla creazione del ticket ? (Così come gli altri dati di Ticket)
	// TODO: Assegnare un livello di priorità per i ticket
	// TODO: Cancellare la possibilità di poter inviare al massimo un messaggio e poi dover aspettare
	//		 la risposta per poterne inviare un altro per i Ticket {OK}
	// TODO: "message doesn't have a default value" per la creazione ticket. Fixare l'errore {OK} - message in ticket
	//		 che non doveva esistere
	// TODO: Creare la PUT per il cambio di stato per il ticket (OPEN → WORKING → CLOSED, OPEN → CLOSED)
	// TODO: Togliere il cambio di stato automatico per i ticket dopo che l'admin risponde.Rispondere ai messaggi
	//  	 non vale più per passare da OPEN a Working/OPEN a CLOSED/OPEN a WORKING A CLOSED (In altre parole
	//		 è possibile avere una conversazione con lo stato del ticket aperto)
	// TODO: Inserire un commento (obbligatorio da FE) per quando si entra all'interno di un asset in manutenzione
	// TODO: Effettuare un collegamento molti a molti tra Ticket e Manutenzione ?
	// TODO: Inserire i sotto-stati per ticket e manutenzione di tipo "Presa in Carico" per quando si lavora con essi
	//		 - ↓ {OK} - Momentaneo
	// TODO: Inserire un attributo per avere lo stato "in lavorazione" per un asset in manutenzione {OK} - Momentaneo
	// TODO: Aggiungere un altro ResponseDto per quando si cambia lo status del ticket/asset manutenzione
	// TODO: Mettere la possibilità di "inProgress" di passare da 1 a 0 dopo un tot. di tempo per gli
	//		 asset in manutenzione {OK}
	// TODO: Fixare l'errore che mostra "Only Admins Can Close Tickets" nel reply.

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
