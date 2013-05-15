/*
 * This file is part of 'Android Interface Goodies'
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
import android.util.AttributeSet;
import android.widget.TextView;

public class ArrayTextView extends TextView {

	public ArrayTextView(Context context) {
		super(context);
	}
	
	public ArrayTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ArrayTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public final void setText(String[] input) {
		setText(input, "\n");
	}
	
	public final void setText(String[] input, String separator) {
		String out = "";
		for (String child : input)
			out = child+separator;
		this.setText(out);
	}

}
