package it.gov.pagopa.swclient.mil.wallet.client;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.wallet.client.bean.PmWalletCardsRequest;

/**
 * Reactive rest client for the REST APIs exposed by the PM-Wallet service
 */
@RegisterRestClient(configKey = "pmwallet-api")
public interface PmWalletService {
	
	/**
	 * Pre-save a payment card in the Wallet
	 * @param version of the API
	 * @param body containing the token of the PAN and the Tax code of the payment card owner
	 * @return the 204 Ack status
	 */
	@POST
	@Path("/cards")
	@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${ocp.apim.subscription}", required = false)
    Uni<Response> cards(@HeaderParam("Version") String version, PmWalletCardsRequest body);

}