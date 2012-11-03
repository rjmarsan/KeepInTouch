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
	
	TYPE type;
	long timestamp;
	int callType;
	String message;
}
