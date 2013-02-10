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

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

public class MainInterface extends ListActivity {
	
	private ArrayList<String> whitelistedSSIDS;
	private ArrayAdapter<String> whitelistAdapter;
	
	private Settings settings;
	private UiGoodies uiGoodies;
	
	// Interface components
	private CheckBox enabledCheckBox;
	private CheckBox notificationCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_interface);
		
		settings = new Settings(this);
		uiGoodies = new UiGoodies(this);
		whitelistedSSIDS = new ArrayList<String>();
		
		whitelistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, whitelistedSSIDS);
		setListAdapter(whitelistAdapter);
		
		enabledCheckBox = (CheckBox) findViewById(R.id.enabled_checkbox);
		notificationCheckBox = (CheckBox) findViewById(R.id.notification_checkbox);
		
		updateUI();
	}

	public void updateUI() {
		if (settings.get("enabled") == 1)
			enabledCheckBox.setChecked(true);
		else
			enabledCheckBox.setChecked(false);
		
		if (settings.get("notifications") == 1)
			notificationCheckBox.setChecked(true);
		else
			notificationCheckBox.setChecked(false);
		
		whitelistedSSIDS.clear();
		ArrayList<String> retrievedWhitelist = new ArrayList<String>();
		retrievedWhitelist = settings.getList("whitelist");
		Iterator<String> whitelistIterator = retrievedWhitelist.iterator();
		while (whitelistIterator.hasNext())
			whitelistedSSIDS.add(whitelistIterator.next());
		
		if (whitelistedSSIDS.size() <= 0)
			findViewById(R.id.whitelistRemoveButton).setVisibility(View.INVISIBLE);
		else
			findViewById(R.id.whitelistRemoveButton).setVisibility(View.VISIBLE);
		
		whitelistAdapter.notifyDataSetChanged();
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

	public void whitelistAddHandler(View view) {		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Enter SSID");
		
		final EditText edit = new EditText(this);
		builder.setView(edit);
			
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String ssidInput = edit.getText().toString();
				if (! ssidInput.isEmpty()) {
					if (whitelistedSSIDS.contains(ssidInput)) {
						uiGoodies.displayToastNotification("Network SSID already in whitelist!");
					}
					else {
						whitelistedSSIDS.add(edit.getText().toString());
						settings.set("whitelist", whitelistedSSIDS);
						updateUI();
					}
				}
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {}
		});

		AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
	}
	
	public void whitelistRemoveHandler(View view) {
		SparseBooleanArray checkedIds = ((ListView) findViewById(android.R.id.list)).getCheckedItemPositions();
		int removedCount = 0;
		for (int i = 0; i < checkedIds.size(); i++) {
			if (checkedIds.get(i) == true) {
				((ListView) findViewById(android.R.id.list)).setItemChecked(i, false);
				whitelistedSSIDS.remove(i-removedCount);
				removedCount++;
			}
		}
		settings.set("whitelist", whitelistedSSIDS);
		updateUI();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main_interface, menu);
		return true;
	}

}
