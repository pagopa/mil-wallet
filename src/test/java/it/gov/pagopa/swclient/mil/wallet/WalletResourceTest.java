package it.gov.pagopa.swclient.mil.wallet;

import static io.restassured.RestAssured.given;

import javax.ws.rs.InternalServerErrorException;
//import io.restassured.response.Response;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.wallet.bean.PmWalletCardsRequestBody;
import it.gov.pagopa.swclient.mil.wallet.bean.SessionResponse;
import it.gov.pagopa.swclient.mil.wallet.bean.WalletRequest;
import it.gov.pagopa.swclient.mil.wallet.client.PmWalletService;
import it.gov.pagopa.swclient.mil.wallet.client.SessionService;
import it.gov.pagopa.swclient.mil.wallet.resource.WalletResource;

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
		
		PmWalletCardsRequestBody body = new PmWalletCardsRequestBody();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequestBody.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NO_CONTENT).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"SessionId", SESSION_ID)
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
						"SessionId", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
	
		
	    Assertions.assertEquals(400, response.statusCode());
	    Assertions.assertEquals("{\"errors\":[\"003000004\"]}", response.getBody().asString());
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
						"SessionId", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
	
		
	    Assertions.assertEquals(500, response.statusCode());
	    Assertions.assertEquals("{\"errors\":[\"003000005\"]}", response.getBody().asString());
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
		
		PmWalletCardsRequestBody body = new PmWalletCardsRequestBody();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequestBody.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NO_CONTENT).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"SessionId", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(400, response.statusCode());
	        Assertions.assertEquals("{\"errors\":[\"003000007\"]}", response.getBody().asString());
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
		
		PmWalletCardsRequestBody body = new PmWalletCardsRequestBody();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequestBody.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NO_CONTENT).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"SessionId", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(400, response.statusCode());
	        Assertions.assertEquals("{\"errors\":[\"003000008\"]}", response.getBody().asString());
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
		
		PmWalletCardsRequestBody body = new PmWalletCardsRequestBody();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequestBody.class)))
		.thenReturn(Uni.createFrom().failure(new InternalServerErrorException()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"SessionId", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(500, response.statusCode());
	        Assertions.assertEquals("{\"errors\":[\"003000006\"]}", response.getBody().asString());
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
		
		PmWalletCardsRequestBody body = new PmWalletCardsRequestBody();
		body.setPanToken(TOKEN);
		body.setTaxCode(TAX_CODE);
		
		Mockito
		.when(pmWalletService.cards(Mockito.eq(API_VERSION), Mockito.any(PmWalletCardsRequestBody.class)))
		.thenReturn(Uni.createFrom().item(Response.status(Status.NOT_IMPLEMENTED).build()));

		io.restassured.response.Response response = given()
				.contentType(ContentType.JSON)
				.headers(
						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
						"Version", API_VERSION,
						"AcquirerId", "4585625",
						"Channel", "ATM",
						"TerminalId", "0aB9wXyZ",
						"SessionId", SESSION_ID)
				.and()
				.body(walletRequest)
				.when()
				.post("/cards")
				.then()
				.extract()
				.response();
			
	        Assertions.assertEquals(500, response.statusCode());
	        Assertions.assertEquals("{\"errors\":[\"003000009\"]}", response.getBody().asString());
	}
	
	
//
//	@Test
//	void testTermsAndCondsGetSessionId_InternalServerError500() {
//		Mockito
//		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
//		.thenReturn(Uni.createFrom().failure(new InternalServerErrorException()));
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(
//						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
//						"Version", API_VERSION,
//						"AcquirerId", "4585625",
//						"Channel", "ATM",
//						"TerminalId", "0aB9wXyZ",
//						"SessionId", SESSION_ID)
//				
//				.and()
//				.when()
//				.post()
//				.then()
//				.extract()
//				.response();
//		
//	    Assertions.assertEquals(500, response.statusCode());
//	    Assertions.assertEquals("{\"errors\":[\"004000003\"]}", response.getBody().asString());
//	     
//	}
//	
//	@Test
//	void testManageOutcomeResponse_InternalServerError500() {
//		SessionResponse sessionResponse = new SessionResponse();
//		sessionResponse.setTaxCode(TAX_CODE);
//		sessionResponse.setOutcome(OUTCOME);
//		
//		Mockito
//		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
//		.thenReturn(Uni.createFrom().item(sessionResponse));
//		
//		Mockito
//		.when(tokenService.getToken(Mockito.any(TokenBody.class)))
//		.thenReturn(Uni.createFrom().failure(new InternalServerErrorException()));
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(
//						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
//						"Version", API_VERSION,
//						"AcquirerId", "4585625",
//						"Channel", "ATM",
//						"TerminalId", "0aB9wXyZ",
//						"SessionId", SESSION_ID)
//				
//				.and()
//				.when()
//				.post()
//				.then()
//				.extract()
//				.response();
//		
//	    Assertions.assertEquals(500, response.statusCode());
//	    Assertions.assertEquals("{\"errors\":[\"004000004\"]}", response.getBody().asString());
//	     
//	}
//	
//	@Test
//	void testSaveNewCard_Failure() {
//		SessionResponse sessionResponse = new SessionResponse();
//		sessionResponse.setTaxCode(TAX_CODE);
//		sessionResponse.setOutcome(OUTCOME);
//		
//		TokenResponse tokenResponse = new TokenResponse();
//		tokenResponse.setToken(TOKEN);
//		
//		TCEntity tcEntity = new TCEntity();
//		
//		Mockito
//		.when(sessionService.getSessionById(Mockito.eq(SESSION_ID), Mockito.any(CommonHeader.class)))
//		.thenReturn(Uni.createFrom().item(sessionResponse));
//		
//		Mockito
//		.when(tokenService.getToken(Mockito.any(TokenBody.class)))
//		.thenReturn(Uni.createFrom().item(tokenResponse));
//		
//		Mockito
//		.when(tcRepository.persistOrUpdate(Mockito.any(TCEntity.class)))
//		.thenReturn(Uni.createFrom().item(tcEntity));
//		
//		Mockito
//		.when(pmWalletService.saveNewCards(TAX_CODE, API_VERSION))
//		.thenReturn(Uni.createFrom().failure(new InternalServerErrorException()));
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(
//						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
//						"Version", API_VERSION,
//						"AcquirerId", "4585625",
//						"Channel", "ATM",
//						"TerminalId", "0aB9wXyZ",
//						"SessionId", SESSION_ID)
//				
//				.and()
//				.when()
//				.post()
//				.then()
//				.extract()
//				.response();
//		
//		  Assertions.assertEquals(201, response.statusCode());
//	      Assertions.assertEquals(false, response.jsonPath().getBoolean("saveNewCards"));
//	    
//	}
//	
//	//TODO:test wrong header params
//	
//	// test GET API
//	@Test
//	void testGetTermsAndConds_200() {
//		
//		TokenResponse tokenResponse = new TokenResponse();
//		tokenResponse.setToken(TOKEN);
//		
//		TCVersion tcV = new TCVersion();
//		tcV.setVersion(tcVersion);
//		TCEntity tcEntity = new TCEntity();
//		tcEntity.version = tcV;
//
//		Mockito
//		.when(tokenService.getToken(Mockito.any(TokenBody.class)))
//		.thenReturn(Uni.createFrom().item(tokenResponse));
////		
//		Mockito
//		.when(tcRepository.findByIdOptional(Mockito.any(String.class)))
//		.thenReturn(Uni.createFrom().item(Optional.of(tcEntity)));
//		
//		
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(
//						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
//						"Version", API_VERSION,
//						"AcquirerId", "4585625",
//						"Channel", "ATM",
//						"TerminalId", "0aB9wXyZ",
//						"SessionId", SESSION_ID)
//				.and()
//				.when()
//				.get("/"+TAX_CODE)
//				.then()
//				.extract()
//				.response();
//			
//	        Assertions.assertEquals(200, response.statusCode());
//	        Assertions.assertEquals("{\"outcome\":\"OK\"}", response.getBody().asString());
//	     
//	}
//	@Test
//	void testGetTermsAndConds_404() {
//		
//		TokenResponse tokenResponse = new TokenResponse();
//		tokenResponse.setToken(TOKEN);
//		
//		Mockito
//		.when(tokenService.getToken(Mockito.any(TokenBody.class)))
//		.thenReturn(Uni.createFrom().item(tokenResponse));
//		
//		Mockito
//		.when(tcRepository.findByIdOptional(Mockito.any(String.class)))
//		.thenReturn(Uni.createFrom().item(Optional.empty()));
//		
//		
//		
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.headers(
//						"RequestId", "d0d654e6-97da-4848-b568-99fedccb642b",
//						"Version", API_VERSION,
//						"AcquirerId", "4585625",
//						"Channel", "ATM",
//						"TerminalId", "0aB9wXyZ",
//						"SessionId", SESSION_ID)
//				.and()
//				.when()
//				.get("/"+TAX_CODE)
//				.then()
//				.extract()
//				.response();
//			
//	        Assertions.assertEquals(404, response.statusCode());
//	        Assertions.assertEquals("{\"errors\":[\"004000006\"]}", response.getBody().asString());
//	     
//	}

}
