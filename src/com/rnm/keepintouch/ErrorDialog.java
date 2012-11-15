package com.rnm.keepintouch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import com.rnm.keepintouch.data.ContactsData.QueryException;
import com.rnm.keepintouch.data.ContactsData.QueryException.QueryError;

public class ErrorDialog {

	public static void show(final Context context, final QueryException exception) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (exception.type == QueryError.SMS) {
	        builder.setTitle(R.string.error_sms_dialog_title);
	        builder.setMessage(R.string.error_sms_dialog_text);
        } else {
	        builder.setTitle(R.string.error_phone_dialog_title);
	        builder.setMessage(R.string.error_phone_dialog_text);
        }
        
        builder.setPositiveButton(R.string.error_dialog_email, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                buildAndSendEmail(context, exception);
            }});
        builder.setNegativeButton(R.string.error_dialog_cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }});
        AlertDialog alert = builder.create();
        
        alert.show();
	}
	
	public static void buildAndSendEmail(Context context, QueryException exception) {
		String title = context.getString(R.string.error_email_title);
		String device = "Manufactuer: "+Build.MANUFACTURER+" Brand: "+Build.BRAND+" Model: "+Build.MODEL;
		String os = "Version: "+Build.VERSION.SDK_INT;
		PackageInfo info;
		String appversion = "";
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			appversion = "appversion: "+info.versionCode+ "name:"+info.versionName+ " installtime:"+info.firstInstallTime;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String error = Log.getStackTraceString(exception.wrappedexception);
		String message = context.getString(R.string.error_email_message, device, os, appversion, exception.message, error);
		sendEmail(context, title, message);
	}

	
	public static void sendEmail(Context context, String subject, String message) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.our_email), ""});
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, message);

		context.startActivity(Intent.createChooser(intent, context.getString(R.string.error_email_picker)));
	}
}
