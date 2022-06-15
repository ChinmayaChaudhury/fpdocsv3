package com.ntuc.vendorservice.vendoradminservice.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;

public enum UserGroupType {
	Vendor("Vendor UserGroup"), FairPrice("FairPrice User Group"); 
	protected String value;

	UserGroupType(String value) {
		this.value = value;
	}

	public static UserGroupType fromValue(String value) {
		for (UserGroupType type : UserGroupType.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		throw new UnknownFormatConversionException("Value passed for enum not known");
	}

	public static List<KeyVal> toKeyValFormat() {
		List<KeyVal> keyVals = new ArrayList<KeyVal>();
		for (UserGroupType type : UserGroupType.values()) {
			keyVals.add(new KeyVal(type.name(), type.value));
		}
		return keyVals;
	}
}