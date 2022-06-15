package com.ntuc.vendorservice.vendoradminservice.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;

public enum SystemUserRole {
	FairPriceAdminstrator("FairPrice Administrator"), FairPriceInternalUser("Fair Price User"), VendorAdministrator(
			"Vendor Administrator"),VendorUser("Vendor User"), UNKOWN("Unknown");
	protected String value;

	SystemUserRole(String value) {
		this.value = value;
	}

	public static SystemUserRole fromValue(String value) {
		for (SystemUserRole type : SystemUserRole.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		throw new UnknownFormatConversionException("Value passed for enum not known");
	}

	public static List<KeyVal> toKeyValFormat() {
		List<KeyVal> keyVals = new ArrayList<KeyVal>();
		for (SystemUserRole type : SystemUserRole.values()) {
			keyVals.add(new KeyVal(type.name(), type.value));
		}
		return keyVals;
	}
}