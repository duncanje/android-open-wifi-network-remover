/*
 * This file is part of 'Settings Goodies'
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

package com.dje.settingsgoodies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.dje.openwifinetworkremover.R;

public class Settings {
	
	public final static int TRUE = 1;
	public final static int FALSE = 0;
	public final static int ERROR = -1;
	public final static int NULL = -2;
	
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
		int length = settings.getInt(key+"Length", ERROR);
		
		int count = 0;
		for (; count < list.size(); count++)
			settingsEditor.putString(key+count, list.get(count));
		
		settingsEditor.putInt(key+"Length", count);
		
		for (; count < length; count++)
			settingsEditor.remove(key+count);
		
		settingsEditor.commit();
	}
	
	// Retrieve a value for a given key, return ERROR constant in case of an error
	public int get(String key) {
		return settings.getInt(key, ERROR);
	}
	
	// Retrieve an ArrayList for a given key
	public ArrayList<String> getList(String key) {
		// Attempt migration on devices with Android 3.0+
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
			migrateToNewStorage(key);
		
		int length = settings.getInt(key+"Length", ERROR);
		ArrayList<String> outList = new ArrayList<String>();
		
		for (int i = 0; i < length; i++)
			outList.add(settings.getString(key+i, "Error"));

		return outList;
	}
	
	// Migrate from the old storage system (0.1-0.1.1) to the new system
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void migrateToNewStorage(String key) {
		HashSet <String> oldList = (HashSet<String>) settings.getStringSet(key, null);
		
		if (oldList != null) {
			ArrayList<String> outList = new ArrayList<String>();
			Iterator<String> iterator = oldList.iterator();
			while (iterator.hasNext())
				outList.add(iterator.next());
			
			set(key, outList);
			settingsEditor.remove(key);
			settingsEditor.commit();
		}
	}
	
	// Useful for debugging - returns all currently stored key-value pairs in a readable form
	public String getAllReadable() {
		return settings.getAll().toString();
	}
}