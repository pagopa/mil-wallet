package it.gov.pagopa.swclient.mil.wallet.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.HeaderParam;

import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.wallet.ErrorCode;

public class WalletHeaderParams extends CommonHeader{

	/*
	 * Request ID
	 */
	@HeaderParam("SessionId")
	@NotNull(message = "[" + ErrorCode.SESSION_ID_MUST_NOT_BE_NULL + "] RequestId must not be null")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + ErrorCode.SESSION_ID_MUST_MATCH_REGEXP + "] RequestId must match \"{regexp}\"")
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	
}
