package entity;

import java.io.Serializable;

public class Result implements Serializable {

	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	private boolean success; //操作是否成功
	private String message; //返回的消息
	
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
