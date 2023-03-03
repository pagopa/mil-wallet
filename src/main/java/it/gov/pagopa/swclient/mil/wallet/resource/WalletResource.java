package it.gov.pagopa.swclient.mil.wallet.resource;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.BeanParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.Errors;
import it.gov.pagopa.swclient.mil.wallet.ErrorCode;
import it.gov.pagopa.swclient.mil.wallet.bean.Outcome;
import it.gov.pagopa.swclient.mil.wallet.bean.WalletHeaderParams;
import it.gov.pagopa.swclient.mil.wallet.bean.WalletRequest;
import it.gov.pagopa.swclient.mil.wallet.client.PmWalletService;
import it.gov.pagopa.swclient.mil.wallet.client.SessionService;
import it.gov.pagopa.swclient.mil.wallet.client.bean.PmWalletCardsRequest;

@Path("/")
public class WalletResource {
	/*
	 * Used to call the Session service to retrieve the 
	 * taxCode and the termsAndCondsAccepted values
	 */
	@RestClient
	private SessionService sessionService;
	
	/*
	 * Used to called the PM-Wallet service
	 */
	@RestClient
	private PmWalletService pmWalletService;
	
	/**
	 * Pre-save the payment card in the internal Wallet
	 * @param headers the object containing all the common headers used by the mil services
	 * @param walletRequest an {@link WalletRequest}
	 * @return NO_CONTENT with no body if no error occurred
	 */
	@POST
	@Path("/cards")
	public Uni<Response> cards(@Valid @BeanParam WalletHeaderParams headers, @Valid WalletRequest walletRequest) {
		Log.debugf("cards - Input parameters: %s, panToken: %s", headers, walletRequest.getPanToken());
		return sessionService.getSessionById(headers.getSessionId(), headers).onFailure()
		.transform(f -> {
			if (f instanceof ClientWebApplicationException c) {
				Log.errorf(f, "[%s] Error while retrieving session. Http Status code [%s] " , ErrorCode.ERROR_SESSION_NOT_FOUND_SERVICE,c.getResponse().getStatus()) ;
				return new BadRequestException(Response
						.status(Status.BAD_REQUEST)
						.entity(new Errors(List.of(ErrorCode.ERROR_SESSION_NOT_FOUND_SERVICE)))
						.build());
			} else {
				Log.errorf(f, "[%s] Error while retrieving session ", ErrorCode.ERROR_CALLING_SESSION_SERVICE);
				return new InternalServerErrorException(Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_CALLING_SESSION_SERVICE)))
					.build());
			}
		})
		.chain (s -> {
			if (Outcome.TERMS_AND_CONDITIONS_NOT_YET_ACCEPTED.toString().equals(s.getOutcome())) {
				Log.errorf("[%s] The terms and conditions are not yet accepted ", ErrorCode.ERROR_TC_NOT_YET_ACCEPTED);
				return Uni.createFrom().item(Response
						.status(Status.BAD_REQUEST)
						.entity(new Errors(List.of(ErrorCode.ERROR_TC_NOT_YET_ACCEPTED)))
						.build());
				
			} else {
				if (Boolean.FALSE.equals(s.getSaveNewCards())) {
					Log.errorf("[%s] Save cards is not active ", ErrorCode.ERROR_SAVE_CARD_NOT_ACTIVE);
					return Uni.createFrom().item(Response
							.status(Status.BAD_REQUEST)
							.entity(new Errors(List.of(ErrorCode.ERROR_SAVE_CARD_NOT_ACTIVE)))
							.build());
				} else {
					Log.debug("Calling PM-Wallet");
					return manageCallPmWallet(s.getTaxCode(),walletRequest.getPanToken(),headers.getVersion());
				}
				
			}
		});
		
	}
	
	/**
	 * Manage the request to the PM-Wallet
	 * @param taxCode: value retrieved from the session service
	 * @param panToken: The token of the PAN passed as body in the request 
	 * @param version of the API
	 * @return the http status
	 */
	private Uni<Response> manageCallPmWallet(String taxCode, String panToken, String version) {
		PmWalletCardsRequest body = new PmWalletCardsRequest();
		body.setPanToken(panToken);
		body.setTaxCode(taxCode);
		return pmWalletService.cards(version, body)
			.onFailure().transform(t-> 
			{
				Log.errorf(t, "[%s] Error calling pm-wallet service ", ErrorCode.ERROR_CALLING_PM_WALLET_SERVICE);
				return new InternalServerErrorException(Response
						.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(List.of(ErrorCode.ERROR_CALLING_PM_WALLET_SERVICE)))
						.build());
			})
			.map(f ->  {
				if (f.getStatus() != Status.NO_CONTENT.getStatusCode()) {
				return Response
						.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(List.of(ErrorCode.ERROR_GENERIC_CALLING_PM_WALLET_SERVICE)))
						.build();
				} else {
					return f;
				}
			});
		
	}
	
}
