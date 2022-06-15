package com.ntuc.vendorservice.foundationcontext.catalog.model;

import java.util.HashMap;
import java.util.Map;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;

public class ResultWithObjectSet extends Result {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,Object> relMap;

	public ResultWithObjectSet() {
		super();
	}
	public ResultWithObjectSet(ResultType resultType, Object result) {
		super(resultType, result); 
	}
	
	public Map<String, Object> getRelMap() { 
		if(relMap==null){
			relMap= new HashMap<>();
		}
		return relMap;
	}
	public Map<String, Object> getRelMap(String key) { 
		if(relMap==null){
			relMap= new HashMap<>();
		}
		return relMap;
	}
	public void addValue(String hashKey, Object hashVal){ 
		if(!getRelMap().containsKey(hashKey)){ 
			getRelMap().put(hashKey, hashVal);
		} 
	}

	public void setRelMap(Map<String, Object> relMap) {
		this.relMap = relMap;
	} 

}
