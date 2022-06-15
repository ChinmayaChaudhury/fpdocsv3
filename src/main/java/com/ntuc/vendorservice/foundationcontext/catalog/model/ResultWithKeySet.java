package com.ntuc.vendorservice.foundationcontext.catalog.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;

public class ResultWithKeySet extends Result {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,List<KeyVal>> keySet;

	public ResultWithKeySet() {
		super();
	}
	public ResultWithKeySet(ResultType resultType, Object result) {
		super(resultType, result); 
	}
	
	public Map<String, List<KeyVal>> getKeySet() { 
		if(keySet==null){
			keySet=new HashMap<String, List<KeyVal>>();
		}
		return keySet;
	}
	public void addKeyValSet(String hashKey, List<KeyVal> hashVal){ 
		if(!getKeySet().containsKey(hashKey)){ 
			getKeySet().put(hashKey, hashVal);
		}else{
			getKeySet().get(hashKey).addAll(hashVal); 
		}
	}

	public void setKeySet(Map<String, List<KeyVal>> keySetMap) {
		this.keySet = keySetMap;
	} 

}
