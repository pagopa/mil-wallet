package it.gov.pagopa.swclient.mil.wallet.bean;

import javax.validation.constraints.Pattern;

import it.gov.pagopa.swclient.mil.wallet.ErrorCode;


public class WalletRequest {

	@Pattern(regexp = "^[a-zA-Z0-9]{1,32}$", message = "[" + ErrorCode.PAN_TOKEN_MUST_MATCH_REGEXP + "] panToken must match \"{regexp}\"")
	private String panToken;

	public String getPanToken() {
		return panToken;
	}

	public void setPanToken(String panToken) {
		this.panToken = panToken;
	}

	@Override
	public String toString() {
		return "InitSessionRequest [panToken=" + panToken + "]";
	}

}
