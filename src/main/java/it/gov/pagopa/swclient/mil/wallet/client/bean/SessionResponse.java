package it.gov.pagopa.swclient.mil.wallet.client.bean;

import javax.validation.constraints.NotNull;

public class SessionResponse {
	/*
	 * T&C accepted or not
	 */
	@NotNull
	private String outcome;

	/*
	 * tax code of the user
	 */
	@NotNull
	private String taxCode;
	
	/*
	 *  If true the client should invoke the presave operation of the Wallet microservice
	 */
	@NotNull
	private Boolean saveNewCards;
	
	/**
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * @param outcome the outcome to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

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
	 * @return the saveNewCards
	 */
	public Boolean getSaveNewCards() {
		return saveNewCards;
	}

	/**
	 * @param saveNewCards the saveNewCards to set
	 */
	public void setSaveNewCards(Boolean saveNewCards) {
		this.saveNewCards = saveNewCards;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SessionResponse [outcome=");
		builder.append(outcome);
		builder.append(", taxCode=");
		builder.append(taxCode);
		builder.append(", saveNewCards=");
		builder.append(saveNewCards);
		builder.append("]");
		return builder.toString();
	}

}
