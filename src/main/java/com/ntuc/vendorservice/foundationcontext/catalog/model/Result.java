package com.ntuc.vendorservice.foundationcontext.catalog.model;

import java.io.Serializable;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;

public class Result  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected  ResultType resultType;
	protected Object result; 
	public Result() {
	}

	public Result(ResultType resultType, Object result) {
		this.result = result;
		this.resultType = resultType;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
