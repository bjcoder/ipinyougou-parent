package com.pinyougou.entity;

import java.io.Serializable;

/**
 * web操作返回结果实体类
 * @author msj
 * 
 */
public class Result implements Serializable{

	private boolean success;
	private String message;
	
	
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
