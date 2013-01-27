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
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.widget.Toast;

public class WifiConnectionHandler extends BroadcastReceiver {

	private WifiManager wifiManager;
	private Settings settings;
	private SupplicantState status;
	private WifiLock lock;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		settings = new Settings(context);
		if (settings.retrieve("enabled") == 1) {
			wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			lock = wifiManager.createWifiLock("Disconnect lock to allow removing network from list");
			
			lock.acquire();
			status = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE); // Get status of wifi connection
			int currentStoredOpenNetworkId = settings.retrieve("currentOpenNetworkId");
			
			Log.d(this.toString(),status.toString());
			
			// Add the network id to settings if connection complete and network is open
			if (status.equals(SupplicantState.COMPLETED) && detectUnsecuredNetwork()) {
				displayToast(context,"Open network (will be forgotten)");
				settings.store("currentOpenNetworkId", wifiManager.getConnectionInfo().getNetworkId());
			}
			// Forgot network and remove id from settings on disconnection if we are connected to an open network
			else if (status.equals(SupplicantState.DISCONNECTED) && currentStoredOpenNetworkId != -1) {
				wifiManager.removeNetwork(currentStoredOpenNetworkId);
				boolean out = wifiManager.saveConfiguration();
				Log.d(this.toString(),out+"");
				settings.store("currentOpenNetworkId", -1); // Reset stored network id
				displayToast(context, "Open network forgotten");
			}
			
			lock.release();
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
