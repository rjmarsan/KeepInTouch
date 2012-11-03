package com.rnm.keepintouch.data;

public class ContactEvent {

	@Override
	public String toString() {
		return "ContactEvent [type=" + type + ", timestamp=" + timestamp
				+ ", callType=" + callType + ", message=" + message + "]";
	}
	public static enum TYPE {
		CALL, SMS
	}
	
	public TYPE type;
	public long timestamp;
	public int callType;
	public String message;
}
