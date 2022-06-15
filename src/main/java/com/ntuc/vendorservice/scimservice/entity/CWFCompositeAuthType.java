package com.ntuc.vendorservice.scimservice.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;

public enum CWFCompositeAuthType {
	Vendor("Vendor Application"),ProspectVendor("Prospect/Temp Vendor Account"), Internal("Internal Applicaiton");
	protected String value;

	CWFCompositeAuthType(String value) {
		this.value = value;
	}

	public static CWFCompositeAuthType fromValue(String value) {
		for (CWFCompositeAuthType type : CWFCompositeAuthType.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		throw new UnknownFormatConversionException("Value passed for enum not known");
	}

	public static List<KeyVal> toKeyValFormat() {
		List<KeyVal> keyVals = new ArrayList<KeyVal>();
		for (CWFCompositeAuthType type : CWFCompositeAuthType.values()) {
			keyVals.add(new KeyVal(type.name(), type.value));
		}
		return keyVals;
	}
}