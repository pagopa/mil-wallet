package it.gov.pagopa.swclient.mil.wallet.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class SessionResponse {
	private String outcome;
	@JsonInclude(Include.NON_NULL)
	private String taxCode;
	@JsonInclude(Include.NON_NULL)
	private Boolean saveNewCards;
	
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	public Boolean isSaveNewCards() {
		return saveNewCards;
	}
	public void setSaveNewCards(Boolean saveNewCards) {
		this.saveNewCards = saveNewCards;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder .append("TermsAndConds session response [outcome=").append(outcome)
				.append(" taxCode=").append(taxCode)
				.append(" saveNewCards=").append(saveNewCards)
				.append("]");
		return builder.toString();
	}
}
