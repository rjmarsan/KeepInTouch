package com.rnm.keepintouch.data;

import android.provider.CallLog;

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
	public String number;
	public int callType;
	public String message;
	
	public boolean isOutgoing() {
		return callType == CallLog.Calls.OUTGOING_TYPE;
	}
}
