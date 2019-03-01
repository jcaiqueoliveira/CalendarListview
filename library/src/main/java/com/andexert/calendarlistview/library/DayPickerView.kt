/***********************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2014 Robin Chutaux
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.andexert.calendarlistview.library

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class DayPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    protected var mAdapter: CalendarAdapter? = null
    private var mController: DatePickerController? = null
    protected var mCurrentScrollState = 0
    protected var mPreviousScrollPosition: Long = 0
    protected var mPreviousScrollState = 0
    private var typedArray: TypedArray? = null
    private var scrollListener: RecyclerView.OnScrollListener? = null

    init {
        if (!isInEditMode) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayPickerView)
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
            init(context)
        }
    }

    fun setController(mController: DatePickerController) {
        this.mController = mController
        setUpAdapter()
        adapter = mAdapter
    }


    fun init(paramContext: Context) {
        layoutManager = LinearLayoutManager(paramContext)
        setUpListView()

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val child = recyclerView.getChildAt(0) as SimpleMonthView ?: return

                mPreviousScrollPosition = dy.toLong()
                mPreviousScrollState = mCurrentScrollState
            }
        }
    }

    protected fun setUpAdapter() {
        if (mAdapter == null) {
            mAdapter = CalendarAdapter(context, mController, typedArray!!)
        }
        mAdapter!!.notifyDataSetChanged()
    }

    protected fun setUpListView() {
        isVerticalScrollBarEnabled = false
        setOnScrollListener(scrollListener)
        setFadingEdgeLength(0)
    }
}