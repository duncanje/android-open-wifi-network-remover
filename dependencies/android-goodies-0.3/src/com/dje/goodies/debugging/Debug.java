/* Copyright (c) 2014 Duncan Eastoe <duncaneastoe@gmail.com>
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
 * Provides helper functionality for debugging
 */

package com.dje.goodies.debugging;

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

import com.dje.goodies.R;

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
