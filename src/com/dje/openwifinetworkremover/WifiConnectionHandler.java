/*
 * This file is part of 'Open Wifi Network Remover'
 * 
 * Copyright 2013 Duncan Eastoe <duncaneastoe@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */

/*
 * Reacts to broadcast intents related to wifi connectivity
 */

package com.dje.openwifinetworkremover;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiConnectionHandler extends BroadcastReceiver {

	private WifiManager wifiManager;
	private Settings settings;
	private SupplicantState status;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		settings = new Settings(context);
		
		status = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE); // Get status of wifi connection
		int currentStoredOpenNetworkId = settings.retrieve("currentOpenNetworkId");
		
		// Add the network id to settings if connection complete and network is open
		if (status.equals(SupplicantState.COMPLETED) && detectUnsecuredNetwork()) {
			displayToast(context,"Open network (will be forgotten)");
			settings.store("currentOpenNetworkId", wifiManager.getConnectionInfo().getNetworkId());
		}
		// Forgot network and remove id from settings on disconnection if we are connected to an open network
		else if (status.equals(SupplicantState.DISCONNECTED) && currentStoredOpenNetworkId != -1) {
			wifiManager.removeNetwork(currentStoredOpenNetworkId);
			wifiManager.saveConfiguration();
			settings.store("currentOpenNetworkId", -1); // Reset stored network id
			displayToast(context, "Open network forgotten");
		}
	}
	
	// Determines the security capabilities of a network
	private boolean detectUnsecuredNetwork() {
		List<ScanResult> scan = wifiManager.getScanResults();
		for (ScanResult network : scan) {
			if (! network.capabilities.contains("WPA")
					&& ! network.capabilities.contains("WEP")
					&& wifiManager.getConnectionInfo().getSSID().equals("\""+network.SSID+"\"")) // Ensure we only detect the network we are connected to
				return true;
		}
		return false;
	}

	// Display toast notification
	private void displayToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.show();
	}
	
}
