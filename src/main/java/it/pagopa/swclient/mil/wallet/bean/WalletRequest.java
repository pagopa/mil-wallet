package it.pagopa.swclient.mil.wallet.bean;

import it.pagopa.swclient.mil.wallet.ErrorCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public class WalletRequest {

	/*
	 * The token of the PAN
	 */
	@NotNull(message = "[" + ErrorCode.PAN_TOKEN_MUST_NOT_BE_NULL + "] panToken must not be null")
	@Pattern(regexp = "^[a-zA-Z0-9]{1,32}$", message = "[" + ErrorCode.PAN_TOKEN_MUST_MATCH_REGEXP + "] panToken must match \"{regexp}\"")
	private String panToken;

	/**
	 * @return the panToken
	 */
	public String getPanToken() {
		return panToken;
	}

	/**
	 * @param panToken the panToken to set
	 */
	public void setPanToken(String panToken) {
		this.panToken = panToken;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WalletRequest [panToken=");
		builder.append(panToken);
		builder.append("]");
		return builder.toString();
	}

}
