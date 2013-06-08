/*
 * This file is part of 'Interface Goodies'
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

package com.dje.interfacegoodies;

import android.content.Context;
import android.widget.Toast;

import com.dje.settingsgoodies.Settings;

public class Goodies {
	
	private Context context;
	
	public Goodies(Context context) {
		this.context = context;
	}
	
	public void displayToastNotification(String message, int enabled) {
		if (enabled != Settings.FALSE)
			displayToastNotification(message);
	}
	
	public void displayToastNotification(String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.show();
	}
	
}
