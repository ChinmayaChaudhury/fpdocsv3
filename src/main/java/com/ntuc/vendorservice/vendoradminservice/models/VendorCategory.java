package com.ntuc.vendorservice.vendoradminservice.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;

public enum VendorCategory {
	L("Large Sized"), M("Medium Sized"), S("Small sized");
	protected String value;

	VendorCategory(String value) {
		this.value = value;
	}

	public static VendorCategory fromValue(String value) {
		for (VendorCategory type : VendorCategory.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		throw new UnknownFormatConversionException("Value passed for enum not known");
	}

	public static List<KeyVal> toKeyValFormat() {
		List<KeyVal> keyVals = new ArrayList<KeyVal>();
		for (VendorCategory type : VendorCategory.values()) {
			keyVals.add(new KeyVal(type.name(), type.value));
		}
		return keyVals;
	}
}