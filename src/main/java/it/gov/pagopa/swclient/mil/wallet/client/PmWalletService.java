package it.gov.pagopa.swclient.mil.wallet.client;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.wallet.bean.PmWalletCardsRequestBody;

@RegisterRestClient(configKey = "pmwallet-api")
public interface PmWalletService {
	
	
	@POST
	@Path("/cards")
    Uni<Response> cards(@HeaderParam("Version") String version, PmWalletCardsRequestBody body);

}
