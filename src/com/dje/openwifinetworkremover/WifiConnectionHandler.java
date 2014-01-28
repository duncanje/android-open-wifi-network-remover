/*
 * This file is part of 'Open Wifi Network Remover'
 * 
 * Copyright 2013-14 Duncan Eastoe <duncaneastoe@gmail.com>
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

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.dje.goodies.settings.Settings;
import com.dje.goodies.ui.Util;

public class WifiConnectionHandler extends BroadcastReceiver {

	private Settings settings;
	private Util toastUtil;
	
	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
	private WifiLock lock;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		settings = new Settings(context);
		if (settings.getInt("enabled") != Settings.FALSE) {
			wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			lock = wifiManager.createWifiLock("Disconnect lock to allow removing network from list");
			lock.acquire();
			
			toastUtil = new Util(context);
			
			SupplicantState connectionStatus = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE); // Get status of wifi connection
			int radioStatus = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, Settings.NULL_INT);
			int currentStoredOpenNetworkId = settings.getInt("currentOpenNetworkId");
			
			wifiInfo = wifiManager.getConnectionInfo();
			
			// Add the network id to settings if connection complete and network is open
			if (SupplicantState.COMPLETED.equals(connectionStatus) && detectAppropriateNetwork()) {
				String currentOpenNetworkSsid = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length()-1);
				toastUtil.displayToastNotification(currentOpenNetworkSsid + " " + context.getString(R.string.network_will_be_forgotten), settings.getInt("notifications"));
				settings.set("currentOpenNetworkId", wifiInfo.getNetworkId());
				settings.set("currentOpenNetworkSsid", currentOpenNetworkSsid);
			}
			
			/* Otherwise, forget and clear the network if an open network id is stored
			 * and the radio is unchanged, enabled or enabling
			 */
			else if (currentStoredOpenNetworkId != Settings.NULL_INT &&
					(radioStatus == Settings.NULL_INT ||
					radioStatus == WifiManager.WIFI_STATE_ENABLED ||
					radioStatus == WifiManager.WIFI_STATE_ENABLING)) {
				wifiManager.removeNetwork(currentStoredOpenNetworkId);
				
				if (wifiManager.saveConfiguration()) {
					String networkIdentifier;
					if (settings.getString("currentOpenNetworkSsid").equals(Settings.NULL_STR))
						networkIdentifier = context.getString(R.string.unknown_network_identifer);
					else
						networkIdentifier = settings.getString("currentOpenNetworkSsid");
					
					toastUtil.displayToastNotification(networkIdentifier + " "
							+ context.getString(R.string.network_forgotten),
							settings.getInt("notifications"));
					
					// Reset stored network details
					settings.set("currentOpenNetworkId", Settings.NULL_INT);
					settings.set("currentOpenNetworkSsid", Settings.NULL_STR);
				}
			}
			
			lock.release();
		}
	}
	
	// Determines the security capabilities of a network
	private boolean detectAppropriateNetwork() {
		List<ScanResult> scan = wifiManager.getScanResults();
		ArrayList<String> whitelist = settings.getList("whitelist");
		String currentBSSID = wifiInfo.getBSSID();
		if (currentBSSID != null) {
			for (ScanResult network : scan) {
				if (currentBSSID.equals(network.BSSID) // Ensure we only detect the network we are connected to
						&& ! whitelist.contains(network.SSID)
						&& ! network.capabilities.contains("WPA")
						&& ! network.capabilities.contains("WEP")
						&& ! network.capabilities.contains("EAP"))
					return true;
			}
		}
		return false;
	}
	
}
