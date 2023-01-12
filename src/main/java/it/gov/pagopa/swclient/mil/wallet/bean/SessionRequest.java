package it.gov.pagopa.swclient.mil.wallet.bean;

public class SessionRequest {
	private boolean termsAndCondsAccepted;	
	private boolean saveNewcards;
	public boolean isTermsAndCondsAccepted() {
		return termsAndCondsAccepted;
	}
	public void setTermsAndCondsAccepted(boolean termsAndCondsAccepted) {
		this.termsAndCondsAccepted = termsAndCondsAccepted;
	}
	public void setSaveNewcards(boolean saveNewcards) {
		this.saveNewcards = saveNewcards;
	}

	public boolean isSaveNewcards() {
		return saveNewcards;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder .append("SessionRequest [termsAndCondsAccepted=").append(termsAndCondsAccepted)
				.append(" saveNewcards=").append(saveNewcards)
				.append("]");
		return builder.toString();
	}
}
