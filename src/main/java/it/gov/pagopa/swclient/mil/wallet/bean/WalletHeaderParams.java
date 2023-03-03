package it.gov.pagopa.swclient.mil.wallet.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.HeaderParam;

import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.wallet.ErrorCode;

public class WalletHeaderParams extends CommonHeader{

	/*
	 * Session ID
	 */
	@HeaderParam("id")
	@NotNull(message = "[" + ErrorCode.SESSION_ID_MUST_NOT_BE_NULL + "] SessionId must not be null")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + ErrorCode.SESSION_ID_MUST_MATCH_REGEXP + "] RequestId must match \"{regexp}\"")
	private String sessionId;

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WalletHeaderParams [sessionId=");
		builder.append(sessionId);
		builder.append("]");
		return builder.toString();
	}
	
}
