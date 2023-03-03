package it.gov.pagopa.swclient.mil.wallet.it;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.gov.pagopa.swclient.mil.wallet.ErrorCode;
import it.gov.pagopa.swclient.mil.wallet.bean.WalletRequest;
import it.gov.pagopa.swclient.mil.wallet.it.resource.Initializer;
import it.gov.pagopa.swclient.mil.wallet.resource.WalletResource;

@QuarkusIntegrationTest
@QuarkusTestResource(value=Initializer.class,restrictToAnnotatedClass = true)
@TestHTTPEndpoint(WalletResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletResourceTestIT {

	final static String API_VERSION			= "1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay";

	//Happy path
	@Test
	void testCards_200() {
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken("123423fwdf");
		
		Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", "e0e222e6-97da-4848-b568-99fedccb642c")
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(204, response.statusCode());
	}
	
	//Sessione inesistente
	@Test
	void testCards_400_sessionIdNotFound() {
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken("123423fwdf");
		
		Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", "c0c444e6-97da-4848-b568-99fedccb642c")
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(400, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.ERROR_SESSION_NOT_FOUND_SERVICE));
	}
	
	
	//T&C non accettate
	@Test
	void testCards_400_TermsAndCondsNotAccepted() {
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken("123423fwdf");
		
		Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", "a2a000d6-97da-4848-b568-99fedccb633a")
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(400, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.ERROR_TC_NOT_YET_ACCEPTED));
	}
	
	//Salvataggio delle nuove carte non attivo
	@Test
	void testCards_400_SaveCardsNotActive() {
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken("123423fwdf");
		
		Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", "a2f987e6-97da-4848-b568-99fedccb643a")
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(400, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.ERROR_SAVE_CARD_NOT_ACTIVE));
	}
	//Errore dal PM-Wallet
	@Test
	void testCards_500_ErrorFromPmWallet() {
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken("00001111");
		
		Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", "e0e222e6-97da-4848-b568-99fedccb642c")
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(500, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.ERROR_GENERIC_CALLING_PM_WALLET_SERVICE));
	}
}
