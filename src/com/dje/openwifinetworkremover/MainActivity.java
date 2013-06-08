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

package com.dje.openwifinetworkremover;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dje.interfacegoodies.Goodies;
import com.dje.settingsgoodies.Settings;

public class MainActivity extends ListActivity implements OnItemClickListener {
	
	private ArrayList<String> whitelistedSSIDS;
	private ArrayAdapter<String> whitelistAdapter;
	
	private Settings settings;
	private Goodies uiGoodies;
	
	// Interface components
	private View settingsLayout;
	private View whitelistLayout;
	private CheckBox enabledCheckBox;
	private CheckBox notificationCheckBox;
	private ListView whitelist;
	private TextView emptyWhitelistLabel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		settings = new Settings(this);
		uiGoodies = new Goodies(this);
		whitelistedSSIDS = new ArrayList<String>();
		
		whitelistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, whitelistedSSIDS);
		setListAdapter(whitelistAdapter);
		
		settingsLayout = (View) findViewById(R.id.settings_layout);
		whitelistLayout = (View) findViewById(R.id.whitelist_layout);
		enabledCheckBox = (CheckBox) findViewById(R.id.enabled_checkbox);
		notificationCheckBox = (CheckBox) findViewById(R.id.notification_checkbox);
		whitelist = (ListView) findViewById(android.R.id.list);
		emptyWhitelistLabel = (TextView) findViewById(R.id.empty_whitelist_label);
		
		// Listen for clicks on whitelist items to allow the action bar items to be altered (Android 3.0+)
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
			whitelist.setOnItemClickListener(this);
		
		updateUI();
	}

	
	// Performs updates to the UI after major events
	public void updateUI() {
		// Set enabled checkbox
		if (settings.get("enabled") == Settings.FALSE) {
			enabledCheckBox.setChecked(false);
			settingsLayout.setVisibility(View.INVISIBLE);
		}
		else {
			enabledCheckBox.setChecked(true);
			settingsLayout.setVisibility(View.VISIBLE);
		}
		
		// Set notifications checkbox
		if (settings.get("notifications") == Settings.FALSE)
			notificationCheckBox.setChecked(false);
		else
			notificationCheckBox.setChecked(true);
		
		// Clear list and re-load it from stored whitelist
		for (int i = 0; i < whitelist.getCount(); i++)
			whitelist.setItemChecked(i, false);
		
		whitelistedSSIDS.clear();
		whitelistedSSIDS.addAll(settings.getList("whitelist"));
		whitelistAdapter.notifyDataSetChanged();
		
		for (int i = 0; i < whitelist.getCount(); i++)
			whitelist.setItemChecked(i, false);
		
		// Show/hide SSID remove button according to whitelist length
		if (whitelistedSSIDS.size() == 0) {
			whitelistLayout.setVisibility(View.INVISIBLE);
			emptyWhitelistLabel.setVisibility(View.VISIBLE);
		}
		else {
			whitelistLayout.setVisibility(View.VISIBLE);
			emptyWhitelistLabel.setVisibility(View.INVISIBLE);
		}
		
		refreshActionBar();
	}
	
	// Handle a click on a checkbox
	public void checkBoxClick(View view) {
		int setting;
		if (((CheckBox) view).isChecked())
			setting = Settings.TRUE;
		else
			setting = Settings.FALSE;
		
		if (view.getId() == R.id.enabled_checkbox)
			settings.set("enabled", setting);
		else if (view.getId() == R.id.notification_checkbox)
			settings.set("notifications", setting);
		
		updateUI();
	}
	
	// Called once on startup to populate initial menu entries
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}
	
	// Called each time a menu is to be opened/has been invalidated
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem removeItem = menu.findItem(R.id.menu_remove);
		MenuItem clearWhitelistItem = menu.findItem(R.id.menu_clear_networks);
		
		// Only display remove option when an item is selected
		if (listItemSelected(whitelist))
			removeItem.setVisible(true);
		else
			removeItem.setVisible(false);
		
		// Only display clear whitelist option when there are items in the whitelist 
		if (whitelistedSSIDS.size() == 0)
			clearWhitelistItem.setVisible(false);
		else
			clearWhitelistItem.setVisible(true);
		
		return super.onPrepareOptionsMenu(menu);
	}


	// Click handler for menu items
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_about) {
			Intent launchAbout = new Intent(this, AboutActivity.class);
			startActivity(launchAbout);
		}
		else if (item.getItemId() == R.id.menu_add) {
			whitelistAdd();
		}
		else if (item.getItemId() == R.id.menu_remove) {
			whitelistRemove();
		}
		else {
			clearNetworks();
		}
	    
	    return super.onOptionsItemSelected(item);
	}
	
	// Click handler for list items
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		refreshActionBar();
	}
	
	// Checks if there is a currently selected list item
	private boolean listItemSelected(ListView list) {
		SparseBooleanArray listItems = list.getCheckedItemPositions();
		
		for (int i = 0; i < listItems.size(); i++) {
			if (listItems.get(i))
				return true;
		}
		return false;
	}

	// Invalidate action bar to cause onPrepareOptionsMenu to be called
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void refreshActionBar() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
			invalidateOptionsMenu();
	}


	// Pops up a dialog box prompting for an SSID and adds it to the whitelist
	private void whitelistAdd() {	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Enter SSID");
		final EditText edit = new EditText(this);
		edit.setSingleLine(true);
		builder.setView(edit);
		final String alreadyInWhitelistMessage = this.getString(R.string.ssid_already_in_whitelist);

		// Handle click on the Ok button
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String ssidInput = edit.getText().toString();
				if (ssidInput.length() > 0) {
					if (whitelistedSSIDS.contains(ssidInput)) {
						uiGoodies.displayToastNotification(ssidInput+" "+alreadyInWhitelistMessage);
					}
					else {
						whitelistedSSIDS.add(edit.getText().toString());
						storeWhitelist();
					}
				}
			}
		});

		// Handle click on the Cancel button
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {}
		});

		AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // Show keyboard
		dialog.show();
	}

	// Removes the selected SSID(s) from the whitelist
	private void whitelistRemove() {
		SparseBooleanArray listItems = whitelist.getCheckedItemPositions();

		int removedCount = 0;
		for (int i = 0; i < listItems.size(); i++) {
			if (listItems.get(i)) {
				whitelistedSSIDS.remove(i-removedCount); // Fix shifted indexes
				removedCount++;
			}
		}

		if (removedCount > 0)
			storeWhitelist();
	}

	// Clear all SSIDs from the whitelist
	private void clearNetworks() {
		whitelistedSSIDS.clear();
		storeWhitelist();
	}
	
	// Store current whitelist in settings and update display
	private void storeWhitelist() {
		settings.set("whitelist", whitelistedSSIDS);
		updateUI();
	}
}
