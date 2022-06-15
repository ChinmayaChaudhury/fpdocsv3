package com.ntuc.vendorservice.foundationcontext.catalog.constants;

/**
 * 
 * @author I305675 
 */
public enum AccountFLPUrl {
    DEV("c50fe82dd","https://apqkwn8xb-qa.accounts.ondemand.com/saml2/idp/sso?sp=https://ap1.hana.ondemand.com/c50fe82dd&idp=apqkwn8xb-qa.accounts.ondemand.com"),
	MAIN("c641d7271","https://flpnwc-c641d7271.dispatcher.ap1.hana.ondemand.com"),
	QAS("c9c29a08f","https://apqkwn8xb-qa.accounts.ondemand.com/saml2/idp/sso?sp=https://ap1.hana.ondemand.com/c9c29a08f&idp=apqkwn8xb-qa.accounts.ondemand.com"),
	PROD("c21b1a448","(https://apqkwn8xb.accounts.ondemand.com/saml2/idp/sso?sp=https://ap1.hana.ondemand.com/c21b1a448&idp=apqkwn8xb.accounts.ondemand.com");
	
	private String account;
	private String url;

	AccountFLPUrl(String account, String url){
		this.account=account;
		this.url=url;
	}
	public String getAccount() {
		return account;
	}
	public String getUrl() {
		return url;
	}

	/**
	 * Resolve the url based on the account
	 * @param requestUrl
	 * @return
	 */
	public static String getUrl(String requestUrl) {
		for(AccountFLPUrl accountFLPUrl:AccountFLPUrl.values()){
			if(requestUrl.contains(accountFLPUrl.account)){
				return accountFLPUrl.url;
			}
		}
		return MAIN.url;
	}
}
