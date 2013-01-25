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
 * Provides simple helper methods to make storing settings easier
 */

package com.dje.openwifinetworkremover;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	
	private SharedPreferences settings;
	
	public Settings(Context context) {
		this.settings = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE);
	}
	
	// Store a given value for a key
	public void store(String key, int value) {
		SharedPreferences.Editor settingsEditor = settings.edit();
		settingsEditor.putInt(key, value);
		settingsEditor.commit();
	}
	
	// Retrieve a value for a given key, return -1 in case of an error
	public int retrieve(String key) {
		return settings.getInt(key, -1);
	}
}
