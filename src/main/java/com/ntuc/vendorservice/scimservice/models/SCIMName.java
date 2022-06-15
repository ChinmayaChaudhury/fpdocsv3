package com.ntuc.vendorservice.scimservice.models;

import java.io.Serializable;

public class SCIMName implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String givenName;

	private String familyName;

	private String honorificPrefix;

	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param givenName
	 *            the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return the familyName
	 */
	public String getFamilyName() {
		return familyName;
	}

	/**
	 * @param familyName
	 *            the familyName to set
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	/**
	 * @return the honorificPrefix
	 */
	public String getHonorificPrefix() {
		return honorificPrefix;
	}

	/**
	 * @param honorificPrefix
	 *            the honorificPrefix to set
	 */
	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
