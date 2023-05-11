package it.pagopa.swclient.mil.wallet.it;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.swclient.mil.wallet.ErrorCode;
import it.pagopa.swclient.mil.wallet.bean.WalletRequest;
import it.pagopa.swclient.mil.wallet.it.resource.Initializer;
import it.pagopa.swclient.mil.wallet.resource.WalletResource;

@QuarkusIntegrationTest
@QuarkusTestResource(value=Initializer.class,restrictToAnnotatedClass = true)
@TestHTTPEndpoint(WalletResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletResourceTestIT {

	final static String API_VERSION			= "1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay";


	Map<String, String> commonHeaders;
	
	@BeforeAll
	void createTestObjects() {
		commonHeaders = new HashMap<>();
		commonHeaders.put("RequestId", "d0d654e6-97da-4848-b568-99fedccb642b");
		commonHeaders.put("Version", API_VERSION);
		commonHeaders.put("AcquirerId", "4585625");
		commonHeaders.put("Channel", "ATM");
		commonHeaders.put("TerminalId", "0aB9wXyZ");
		commonHeaders.put("SessionId", "e0e222e6-97da-4848-b568-99fedccb642c");
	}
	
	//Happy path
	@Test
	void testCards_200() {
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken("123423fwdf");
		

		
		Response response = given()
				.contentType(ContentType.JSON)
//				.headers(commonHeaders)
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
	
//	//Sessione inesistente
//	@Test
//	void testCards_400_sessionIdNotFound() {
//		
//		WalletRequest walletRequest = new WalletRequest();
//		walletRequest.setPanToken("123423fwdf");
//		
//		commonHeaders.put("SessionId", "e0e222e6-97da-4848-b568-99fedccb642c");
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(commonHeaders)
////				.headers(
////						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
////						"Version", API_VERSION,
////						"AcquirerId", "4585625",
////						"Channel", "ATM",
////						"TerminalId", "0aB9wXyZ",
////						"id", "c0c444e6-97da-4848-b568-99fedccb642c")
//				.and()
//				.body(walletRequest)
//				.when()
//				.post("/cards")
//				.then()
//				.extract()
//				.response();
//			
//	        Assertions.assertEquals(400, response.statusCode());
//	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.SESSION_NOT_FOUND_ERROR));
//	}
//	
//	
//	//T&C non accettate
//	@Test
//	void testCards_400_TermsAndCondsNotAccepted() {
//		
//		WalletRequest walletRequest = new WalletRequest();
//		walletRequest.setPanToken("123423fwdf");
//		
//		commonHeaders.put("SessionId", "a2a000d6-97da-4848-b568-99fedccb633a");
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(commonHeaders)
////				.headers(
////						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
////						"Version", API_VERSION,
////						"AcquirerId", "4585625",
////						"Channel", "ATM",
////						"TerminalId", "0aB9wXyZ",
////						"id", "a2a000d6-97da-4848-b568-99fedccb633a")
//				.and()
//				.body(walletRequest)
//				.when()
//				.post("/cards")
//				.then()
//				.extract()
//				.response();
//			
//	        Assertions.assertEquals(400, response.statusCode());
//	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.TC_NOT_YET_ACCEPTED_ERROR));
//	}
//	
//	//Salvataggio delle nuove carte non attivo
//	@Test
//	void testCards_400_SaveCardsNotActive() {
//		
//		WalletRequest walletRequest = new WalletRequest();
//		walletRequest.setPanToken("123423fwdf");
//		
//		commonHeaders.put("SessionId", "a2a000d6-97da-4848-b568-99fedccb633a");
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(commonHeaders)
////				.headers(
////						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
////						"Version", API_VERSION,
////						"AcquirerId", "4585625",
////						"Channel", "ATM",
////						"TerminalId", "0aB9wXyZ",
////						"id", "a2f987e6-97da-4848-b568-99fedccb643a")
//				.and()
//				.body(walletRequest)
//				.when()
//				.post("/cards")
//				.then()
//				.extract()
//				.response();
//			
//	        Assertions.assertEquals(400, response.statusCode());
//	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.SAVE_CARD_NOT_ACTIVE_ERROR));
//	}
//	//Errore dal PM-Wallet
//	@Test
//	void testCards_500_ErrorFromPmWallet() {
//		
//		WalletRequest walletRequest = new WalletRequest();
//		walletRequest.setPanToken("00001111");
//		
//		commonHeaders.put("SessionId", "e0e222e6-97da-4848-b568-99fedccb642c");
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(commonHeaders)
////				.headers(
////						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
////						"Version", API_VERSION,
////						"AcquirerId", "4585625",
////						"Channel", "ATM",
////						"TerminalId", "0aB9wXyZ",
////						"id", "e0e222e6-97da-4848-b568-99fedccb642c")
//				.and()
//				.body(walletRequest)
//				.when()
//				.post("/cards")
//				.then()
//				.extract()
//				.response();
//			
//	        Assertions.assertEquals(500, response.statusCode());
//	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.GENERIC_ERROR_CALLING_PM_WALLET_SERVICE));
//	}
}
