package com.dje.openwifinetworkremover;

import android.content.Context;
import android.widget.Toast;

public class UiGoodies {
	
	private Context context;
	
	public UiGoodies(Context context) {
		this.context = context;
	}
	
	public void displayToastNotification(String message, int enabled) {
		if (enabled == 1)
			displayToastNotification(message);
	}
	
	public void displayToastNotification(String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.show();
	}
}
