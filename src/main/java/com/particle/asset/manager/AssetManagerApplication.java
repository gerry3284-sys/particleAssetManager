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

	// TODO: Per il nome dello storage usare un enum (inserire più tipi)
	// TODO: Per la quantità di spazio dello storage usare un enum
	// TODO: Implementare la creazione del token attraverso l'Office 365 (token SDK) - TBA
	// TODO: Cancellare la creazione del token attraverso il database {OK}
	// TODO: Modificare da "boolean" a "Boolean" (per avere "NULL") ?
	//		 Potrebbe avere senso per far si che serva obbligatoriamente il valore
	// TODO: Modificare il salvataggio del file rendendolo obbligatorio in un'operazione POST del Movement
	// TODO: Creare un "Operations" in "enums" anche per "User" ?
	// TODO: Se si dovesse togliere Hard Disk e Ram di un Tipo di Asset deve azzerarsi a DB ?
	// TODO: Restituire il nome dello statusCode per "GET user/{code}/movement" ?
	// TODO: Inserire OID (che si prenderà da login SDK) nella tabella users {OK}
	// TODO: Inserire OID nella creazione del DB e nell'inserimento dei valori di default {OK}
	// TODO: Risolvere il problema del Response Body sbagliato per la creazione di un AssetType {OK}
	// TODO: Inserire un controllo che faccia si che non si possa inserire 0 come valore RAM
	//		 se l'assetType lo possiede (stesso discorso per lo storage).
	// TODO: Quando si prova a inserire un Asset che esiste già, compare l'errore sbagliato
	// TODO: Inserire in tutti i controlli e set (dove necessario) {OK}

	public static void main(String[] args) {
		SpringApplication.run(AssetManagerApplication.class, args);
	}
}
