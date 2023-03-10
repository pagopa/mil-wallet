package it.gov.pagopa.swclient.mil.wallet.client.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PmWalletCardsRequest {
	
	/*
	 * Tax code of the payment card owner
	 */
	@NotNull
	@Pattern(regexp = "[a-zA-Z]{6}\\d{2}[a-zA-Z]\\d{2}[a-zA-Z]\\d{3}[a-zA-Z]")
	private String taxCode;
	
	/*
	 * The token of the PAN
	 */
	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9]{1,32}$")
	private String panToken;
	/**
	 * @return the taxCode
	 */
	public String getTaxCode() {
		return taxCode;
	}
	/**
	 * @param taxCode the taxCode to set
	 */
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
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
		builder.append("PmWalletCardsRequest [taxCode=");
		builder.append(taxCode);
		builder.append(", panToken=");
		builder.append("***");
		builder.append("]");
		return builder.toString();
	}


}
