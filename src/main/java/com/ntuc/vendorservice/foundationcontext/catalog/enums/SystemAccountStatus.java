package com.ntuc.vendorservice.foundationcontext.catalog.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;

public enum SystemAccountStatus {
	ACTIVE("Active"), NEW("Newly Created"), INACTIVE("In-active");
	protected String value;

	SystemAccountStatus(String value) {
		this.value = value;
	}

	public static SystemAccountStatus fromValue(String value) {
		for (SystemAccountStatus type : SystemAccountStatus.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		throw new UnknownFormatConversionException("Value passed for enum not known");
	}

	public static List<KeyVal> toKeyValFormat() {
		final List<KeyVal> entries = new ArrayList<>();
		for (SystemAccountStatus type : SystemAccountStatus.values()) {
			entries.add(new KeyVal(type.name(), type.value));
		}
		return entries;
	}
}