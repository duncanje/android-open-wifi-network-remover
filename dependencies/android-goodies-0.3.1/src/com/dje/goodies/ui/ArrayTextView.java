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

package com.dje.goodies.ui;

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
