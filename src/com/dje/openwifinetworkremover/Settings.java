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

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	
	public final static int TRUE = 1;
	public final static int FALSE = 0;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor settingsEditor;
	
	public Settings(Context context) {
		this.settings = context.getSharedPreferences(context.getString(R.string.settings_key), Context.MODE_PRIVATE);
		settingsEditor = settings.edit();
	}
	
	// Store a value for a given key
	public void set(String key, int value) {
		settingsEditor.putInt(key, value);
		settingsEditor.commit();
	}
	
	// Convert an ArrayList to a separated string and store it
	public void set(String key, ArrayList<String> list) {
		int whitelistLength = settings.getInt("whitelistLength", -1);
		
		Iterator<String> listIterator = list.iterator();
		int count = 0;
		while (listIterator.hasNext()) {
			settingsEditor.putString("whitelist"+count, listIterator.next());
			count++;
		}
		settingsEditor.putInt("whitelistLength", count);
		
		if (count < whitelistLength) {
			for (int i = count; i < whitelistLength; i++)
				settingsEditor.remove("whitelist"+i);
		}
		
		settingsEditor.commit();
	}
	
	// Retrieve a value for a given key, return -1 in case of an error
	public int get(String key) {
		return settings.getInt(key, -1);
	}
	
	// Retrieve an ArrayList for a given key
	public ArrayList<String> getList(String key) {
		//TODO Method to migrate data to the new storage system
		int whitelistLength = settings.getInt("whitelistLength", -1);
		ArrayList<String> outList = new ArrayList<String>();
		
		for (int i = 0; i < whitelistLength; i++)
			outList.add(settings.getString("whitelist"+i, "Error"));

		return outList;
	}
}
