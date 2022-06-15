package com.ntuc.vendorservice.foundationcontext.repository;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;

public class DatabaseExceptionsHelper {
	
	public static <T>Boolean  isUniqueConstraintViolation(Exception e, T t) throws AccountManagementException {
	    if (e.getMessage().contains("unique constraint violated")) {
	    	throw new AccountManagementException(getViolationMessage(t.getClass().getDeclaredFields()));
	    } 
	    return false;
	} 
	public static <T>Boolean  isUniqueConstraintViolation(Exception e) throws AccountManagementException {
	    if (e.getMessage().contains("unique constraint violated")) {
	    	throw new AccountManagementException("Record already exist");
	    } 
	    return false;
	} 
	private static String getViolationMessage(Field[] declaredFields) {
		StringBuilder  stringBuilder=new StringBuilder();
		stringBuilder.append("Any of these fields have been violated");
		Set<String> violatedFields=new HashSet<String>();
		for (Field field : declaredFields) {
			field.setAccessible(true);
			Column annotation= field.getAnnotation(Column.class);
			if (annotation != null) {
				boolean unique = annotation.unique();
				if(unique){ 
					violatedFields.add(field.getName());
				} 
			}
		} 
		return stringBuilder.append(violatedFields.toString()).toString();
	}
}
