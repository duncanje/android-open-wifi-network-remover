/* Copyright (c) 2013-14 Duncan Eastoe <duncaneastoe@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * Provides simple helper methods to make storing settings easier
 */

package com.dje.goodies.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.dje.goodies.R;

public class Settings {
	
	public final static int TRUE = 1;
	public final static int FALSE = 0;
	public final static int ERROR = -1;
	public final static int NULL_INT = -2;
	public final static String NULL_STR = "";
	
	private SharedPreferences settings;
	private SharedPreferences.Editor settingsEditor;
	
	public Settings(Context context) {
		this.settings = context.getSharedPreferences(context.getString(R.string.settings_key), Context.MODE_PRIVATE);
		settingsEditor = settings.edit();
	}
	
	// Store an integer value for a given key
	public void set(String key, int value) {
		settingsEditor.putInt(key, value);
		settingsEditor.commit();
	}
	
	// Store a string value for a given key
	public void set(String key, String value) {
		settingsEditor.putString(key, value);
		settingsEditor.commit();
	}
	
	// Convert an ArrayList to a separated string and store it
	public void set(String key, List<String> list) {
		int length = settings.getInt(key+"Length", ERROR);
		
		int count = 0;
		for (; count < list.size(); count++)
			settingsEditor.putString(key+count, list.get(count));
		
		settingsEditor.putInt(key+"Length", count);
		
		for (; count < length; count++)
			settingsEditor.remove(key+count);
		
		settingsEditor.commit();
	}
	
	// Retrieve an integer value for a given key, return ERROR constant in case of an error
	public int getInt(String key) {
		return settings.getInt(key, ERROR);
	}
	
	// Retrieve a string value for a given key
	public String getString(String key) {
		return settings.getString(key, NULL_STR);
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
	
	// Retrieve SharedPreferences object
	public SharedPreferences getSettings() {
		return settings;
	}
	
	// Useful for debugging - returns all currently stored key-value pairs in a readable form
	public String getAllReadable() {
		return settings.getAll().toString();
	}
}
