package it.pagopa.swclient.mil.wallet;

import static io.restassured.RestAssured.given;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.bean.CommonHeader;
import it.pagopa.swclient.mil.wallet.ErrorCode;
import it.pagopa.swclient.mil.wallet.bean.WalletRequest;
import it.pagopa.swclient.mil.wallet.client.PmWalletService;
import it.pagopa.swclient.mil.wallet.client.SessionService;
import it.pagopa.swclient.mil.wallet.client.bean.PmWalletCardsRequest;
import it.pagopa.swclient.mil.wallet.client.bean.SessionResponse;
import it.pagopa.swclient.mil.wallet.resource.WalletResource;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
@TestHTTPEndpoint(WalletResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletResourceTest {
	
	final static String SESSION_ID				= "a6a666e6-97da-4848-b568-99fedccb642c";
	final static String TAX_CODE				= "CHCZLN73D08A662B";
	final static String OUTCOME_NOT_ACCEPTED	= "TERMS_AND_CONDITIONS_NOT_YET_ACCEPTED";
	final static String OUTCOME_OK				= "OK";
	final static String TOKEN					= "XYZ13243XXYYZZ";
	final static String API_VERSION				= "1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay";
	final static String TC_VERSION				= "1";
	
	@InjectMock
	@RestClient
	private SessionService sessionService;
	
	@InjectMock
	@RestClient
	private PmWalletService pmWalletService;
	
	// test POST API
	@Test
	void testWalletResource_200() {
		SessionResponse sessionResponse = new SessionResponse();
		sessionResponse.setTaxCode(TAX_CODE);
		sessionResponse.setOutcome(OUTCOME_OK);
		sessionResponse.setSaveNewCards(true);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken(TOKEN);
		
		Mockito
		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
		.thenReturn(Uni.createFrom().item(sessionResponse));
		
		PmWalletCardsRequest body = new PmWalletCardsRequest();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequest.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NO_CONTENT).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(204, response.statusCode());
	     
	}
	
	@Test
	void testWalletResource_SessionIdNotFound_404() {
		Mockito
			.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
			.thenReturn(Uni.createFrom().failure(new ClientWebApplicationException(404)));
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken(TOKEN);
		
		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
	
		
	    Assertions.assertEquals(400, response.statusCode());
	    Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.SESSION_NOT_FOUND_ERROR));
	}
	
	@Test
	void testWalletResourceSessionServiceReturn_500() {
		Mockito
			.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
			.thenReturn(Uni.createFrom().failure(new InternalServerErrorException()));
		
		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken(TOKEN);
		
		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
	
		
	    Assertions.assertEquals(500, response.statusCode());
	    Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.ERROR_CALLING_SESSION_SERVICE));
	}
	
	@Test
	void testWalletResource_TC_Not_Yet_Accepted() {
		SessionResponse sessionResponse = new SessionResponse();
		sessionResponse.setTaxCode(TAX_CODE);
		sessionResponse.setOutcome(OUTCOME_NOT_ACCEPTED);
		sessionResponse.setSaveNewCards(true);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken(TOKEN);
		
		Mockito
		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
		.thenReturn(Uni.createFrom().item(sessionResponse));
		
		PmWalletCardsRequest body = new PmWalletCardsRequest();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequest.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NO_CONTENT).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(400, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.TC_NOT_YET_ACCEPTED_ERROR));
	}
	
	@Test
	void testWalletResource_SaveCard_NotActive() {
		SessionResponse sessionResponse = new SessionResponse();
		sessionResponse.setTaxCode(TAX_CODE);
		sessionResponse.setOutcome(OUTCOME_OK);
		sessionResponse.setSaveNewCards(false);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken(TOKEN);
		
		Mockito
		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
		.thenReturn(Uni.createFrom().item(sessionResponse));
		
		PmWalletCardsRequest body = new PmWalletCardsRequest();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequest.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NO_CONTENT).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(400, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.SAVE_CARD_NOT_ACTIVE_ERROR));
	}
	
	@Test
	void testWalletResource_PmWallet_500() {
		SessionResponse sessionResponse = new SessionResponse();
		sessionResponse.setTaxCode(TAX_CODE);
		sessionResponse.setOutcome(OUTCOME_OK);
		sessionResponse.setSaveNewCards(true);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken(TOKEN);
		
		Mockito
		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
		.thenReturn(Uni.createFrom().item(sessionResponse));
		
		PmWalletCardsRequest body = new PmWalletCardsRequest();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequest.class)))
		.thenReturn(Uni.createFrom().failure(new InternalServerErrorException()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(500, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.ERROR_CALLING_PM_WALLET_SERVICE));
	}
	
	@Test
	void testWalletResource_PmWallet_NotReturn_204() {
		SessionResponse sessionResponse = new SessionResponse();
		sessionResponse.setTaxCode(TAX_CODE);
		sessionResponse.setOutcome(OUTCOME_OK);
		sessionResponse.setSaveNewCards(true);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setPanToken(TOKEN);
		
		Mockito
		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
		.thenReturn(Uni.createFrom().item(sessionResponse));
		
		PmWalletCardsRequest body = new PmWalletCardsRequest();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequest.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NOT_IMPLEMENTED).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"id", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(500, response.statusCode());
	        Assertions.assertTrue(response.jsonPath().getList("errors").contains(ErrorCode.GENERIC_ERROR_CALLING_PM_WALLET_SERVICE));
	}
	

}
