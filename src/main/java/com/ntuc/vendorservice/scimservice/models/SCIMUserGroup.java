package com.ntuc.vendorservice.scimservice.models;

import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

/**
 * The Enum SCIMUserGroup.
 */
public enum SCIMUserGroup {

	/** The Internal documents administrator. */
	InternalDocumentsAdministrator("FairPrice Administrator"),
	/** The Internal documents end user. */
	InternalDocumentsEndUser("Fair Price User"),
	/** The Vendor documents administrator. */
	VendorDocumentsAdministrator("Vendor Administrator group"),
	/** The Vendor documents end user. */
	VendorDocumentsEndUser("Vendor User");

	/** The value. */
	protected String value;

	/**
	 * Instantiates a new SCIM user group.
	 *
	 * @param value
	 *            the value
	 */
	SCIMUserGroup(String value) {
		this.value = value;
	}

	/**
	 * From value.
	 *
	 * @param value
	 *            the value
	 * @return the SCIM user group
	 */
	public static SCIMUserGroup fromValue(String value) {
		for (SCIMUserGroup type : SCIMUserGroup.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		throw new UnknownFormatConversionException(
				"Value passed for enum not known");
	}

	/**
	 * To key val format.
	 *
	 * @return the list
	 */
	public static List<KeyVal> toKeyValFormat() {
		List<KeyVal> keyVals = new ArrayList<KeyVal>();
		for (SCIMUserGroup type : SCIMUserGroup.values()) {
			keyVals.add(new KeyVal(type.name(), type.value));
		}
		return keyVals;
	}
}