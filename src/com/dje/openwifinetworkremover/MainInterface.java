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
 * This will be the future preferences interface, it currently doesn't do much!
 */

package com.dje.openwifinetworkremover;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

public class MainInterface extends Activity {
	
	private Settings settings;
	
	// Interface components
	private CheckBox enabledCheckBox;
	private CheckBox notificationCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_interface);
		
		settings = new Settings(this);
		
		enabledCheckBox = (CheckBox) findViewById(R.id.enabled_checkbox);
		notificationCheckBox = (CheckBox) findViewById(R.id.notification_checkbox);
		
		setupUI();
	}

	public void setupUI() {
		if (settings.get("enabled") == 1)
			enabledCheckBox.setChecked(true);
		else
			enabledCheckBox.setChecked(false);
		
		if (settings.get("notifications") == 1)
			notificationCheckBox.setChecked(true);
		else
			notificationCheckBox.setChecked(false);
	}
	
	public void checkBoxClick(View view) {
		boolean checked = ((CheckBox) view).isChecked();
		
		if (view.getId() == R.id.enabled_checkbox) {
			if (checked)
				settings.set("enabled", 1);
			else
				settings.set("enabled", 0);
		}
		if (view.getId() == R.id.notification_checkbox) {
			if (checked)
				settings.set("notifications", 1);
			else
				settings.set("notifications", 0);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_interface, menu);
		return true;
	}

}
