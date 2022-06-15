package com.ntuc.vendorservice.scimservice.models;

import java.io.Serializable;

public class SCIMPhoneNumber implements Serializable{
	public static enum  Type{
		work,fax,mobile;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String type;

	private String value;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
 
     

}
