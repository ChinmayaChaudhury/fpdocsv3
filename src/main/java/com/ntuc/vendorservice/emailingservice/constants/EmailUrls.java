package com.ntuc.vendorservice.emailingservice.constants;

public enum EmailUrls {
	 DOC_SHARE_EMAIL("/fp.docs/send/ds"),
	 ACTIVATION_EMAIL("/fp.docs/send/act"),
	 CWF_ACTIVATION_EMAIL("/fp.docs/send/cwf_act"),
	 CWF_REQUEST_EMAIL("/fp.docs/mail/test"),
	 CWF_REQUEST_NO_ATTACHMENT_EMAIL("/fp.docs/mail/noattachment"),
	 CWF_REQUEST_ATTACHMENT_EMAIL("/fp.docs/mail/attachment"), UNKNOWN("");
	
	protected String value;

	EmailUrls(String value) {
		this.value = value;
	}

	public static EmailUrls fromValue(String value) {
		for (EmailUrls type : EmailUrls.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return EmailUrls.UNKNOWN;
	} 
}
