/*
 * ErrorCode.java
 *
 * 12 dec 2022
 */

package it.gov.pagopa.swclient.mil.wallet;

/**
 * 
 * @author Antonio Tarricone
 */
public final class ErrorCode {
	public static final String MODULE_ID 								= "003";
	public static final String PAN_TOKEN_MUST_MATCH_REGEXP				= MODULE_ID + "000001";
	public static final String SESSION_ID_MUST_NOT_BE_NULL				= MODULE_ID + "000002";
	public static final String SESSION_ID_MUST_MATCH_REGEXP				= MODULE_ID + "000003";
	public static final String ERROR_SESSION_NOT_FOUND_SERVICE			= MODULE_ID + "000004";
	public static final String ERROR_CALLING_SESSION_SERVICE			= MODULE_ID + "000005";
	public static final String ERROR_CALLING_PM_WALLET_SERVICE			= MODULE_ID + "000006";
	public static final String ERROR_TC_NOT_YET_ACCEPTED				= MODULE_ID + "000007";
	public static final String ERROR_SAVE_CARD_NOT_ACTIVE				= MODULE_ID + "000008";
	public static final String ERROR_GENERIC_CALLING_PM_WALLET_SERVICE	= MODULE_ID + "000009";
	

	private ErrorCode() {
	}
}
