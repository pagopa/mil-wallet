package it.pagopa.swclient.mil.wallet.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.wallet.client.bean.PmWalletCardsRequest;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

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