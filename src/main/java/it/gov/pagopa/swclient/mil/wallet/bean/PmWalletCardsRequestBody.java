package it.gov.pagopa.swclient.mil.wallet.bean;

public class PmWalletCardsRequestBody {
	
	private String taxCode;
	private String panToken;
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public String getPanToken() {
		return panToken;
	}
	public void setPanToken(String panToken) {
		this.panToken = panToken;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder .append("TermsAndConds session response [panToken=").append(panToken)
				.append(" taxCode=").append(taxCode)
				.append("]");
		return builder.toString();
	}
}
