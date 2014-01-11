/*
 * This file is part of 'Open Wifi Network Remover'
 * 
 * Copyright 2014 Duncan Eastoe <duncaneastoe@gmail.com>
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
 * Provides helper functionality for debugging
 */

package com.dje.openwifinetworkremover;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.LogPrinter;
import android.util.PrintWriterPrinter;
import android.util.Printer;

public class Debug {

	public final static boolean DEBUG_ENABLED = true;
	
	private Printer outputPrinter;
	private PrintWriter outputPrintWriter;
	private File outputDir, outputFile;
	private Long currentTime;
	
	private boolean externalAvailable = false;
	
	public Debug(Context context) {
		if (DEBUG_ENABLED) {
			// Only attempt output to file if external storage is available
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				outputDir = new File(Environment.getExternalStorageDirectory(),
						context.getString(R.string.app_name) + "/debug");
				outputDir.mkdirs();
				
				// Debug output file has timestamp as the name
				currentTime = Long.valueOf(Calendar.getInstance().getTimeInMillis());
				outputFile = new File(outputDir, currentTime.toString());
				
				try {
					outputPrintWriter = new PrintWriter(outputFile);
					outputPrinter = new PrintWriterPrinter(outputPrintWriter);
					externalAvailable = true;
				} catch (FileNotFoundException e) {
					System.out.println("Failed to open debug file " + e);
				}
			}
			
			// If external storage isn't available then do logging instead
			if (! externalAvailable) {
				outputPrinter = new LogPrinter(Log.DEBUG, context.getString(R.string.app_name));
			}
		}
	}
	
	// Output the given debugging text
	public void write(String text) {
		if (DEBUG_ENABLED) {
			outputPrinter.println(text);
			if (externalAvailable)
				outputPrintWriter.flush();
		}
	}
	
	// Close output writer
	public void close() {
		if (DEBUG_ENABLED && externalAvailable)
			outputPrintWriter.close();
	}
	
}
