package com.feelpair.xy.handlers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class MessageHandler {

	public static void logException(Exception e) {
		if (e != null) {
			e.printStackTrace();
		}
	}

	public static void showToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	public static void showToast(Context context, String msg) {
		if (context != null) {
			showToast(context, msg, 0);
		}
	}

	public static void showFailure(Context context) {
		showToast(context, "网络不佳");
	}

	public static void showLast(Context context) {
		showToast(context, "没有数据了");
	}

	public static void showFailure(Context context, Exception e) {
		showFailure(context);
		logException(e);
	}

}
