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
	// 422 → Unprocessable Entity, 423 → Table State is Blocked
	// TODO: id degli admin → 22e19954-a4df-4a0a-827f-147cf41299ac
	// TODO: id degli utenti → 8f2da0d5-e088-4e25-b11b-5eb3f7524597
	// /me/memberOf → Per recuperare ↑ (chiedere ad Alessandro per quanto riguarda il token Graph)
	// Chiamare sia /me (per dati) e /memberOf (per group)

	// Operazioni da Effettuare
	// TODO: Modificare il salvataggio del file rendendolo obbligatorio in un'operazione POST del Movement
	// TODO: Modificare la PUT per lo status dell'asset {OK} - La modifica è temporanea. Da cambiare con gli enum
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
	// TODO: La Data di Fine Manutenzione può essere uguale alla data odierna ?
	// TODO: Fixare la riconsegna di un asset {Fix temporaneo per via del fatto che è stata semplicemente tolta
	//		 la priorità, dato che non si sa come usarla per bene in questo caso}
	// TODO: Far si che il pdf venga creato solo se l'operazione di movimento va a buon fine
	// TODO: Inserire l'obbligo di autorizzarsi quando si usa Swagger
	// TODO: Dare la possibilità di modificare le Note di un movimento

	// Cambiamenti Effettuati
	// 1)	Fixata la riconsegna di un asset {Temporaneo per via del fatto che è stata semplicemente tolta
	// 		la priorità, dato che non si sa come usarla per bene in questo caso}
	// 2)	Inserito un attributo "outdated" che viene messo a true quando viene modificato un asset (tranne le note)
	// 3)	La priorità di un ticket è stata messa nullable perchè solo l'admin la può modificare
	// 4)	Il tipo del "tipo di richiesta" è stato modificato da "MovementType" a "String"
	// 5)	Aggiunti gli errori per l'aggiornamento di un asset se la BU o l'Asset Type non vengono trovati
	// 6)	Inserito un controllo per vedere se il ticket è per un progetto interno o esterno.
	//		In quest'ultimo caso, viene inserita la priorità HIGH
	// 7)	Modificata la creazione del ticket per far si che non sia più necessario avere un assetCode o
	//		assetTypeCode != null per funzionare (adesso possono essere entrambi null)
	// 8)	Modificato il limite massimo di caratteri inseribili in una nota per gli Asset da 255 a 500
	// 9)	Modificato il limite massimo di caratteri inseribili in una nota per i movimenti da 255 a 250
	// 10)	Modificato il tipo per "operation" di della tabella ticket da ENUM in VARCHAR
	// 11) 	Aggiunti i controlli per le note dei punti 8 e 9

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
