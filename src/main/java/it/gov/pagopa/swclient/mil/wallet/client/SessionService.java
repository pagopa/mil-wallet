package it.gov.pagopa.swclient.mil.wallet.client;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.wallet.bean.SessionResponse;

@Path("/sessions")
@RegisterRestClient(configKey = "session-api")
public interface SessionService {
	
	@GET
	@Path("/{sessionId}")
    Uni<SessionResponse> getSessionById(@PathParam("sessionId") String sessionId, @BeanParam CommonHeader commonHeader);
	
}
