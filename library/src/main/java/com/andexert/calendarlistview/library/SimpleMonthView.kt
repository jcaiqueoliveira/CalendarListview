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
import android.graphics.Canvas
import android.text.format.DateUtils
import android.text.format.Time
import android.view.MotionEvent
import android.view.View
import java.security.InvalidParameterException
import java.text.DateFormatSymbols
import java.util.*

internal class SimpleMonthView(context: Context, typedArray: TypedArray) : View(context) {

    protected var mPadding = 0

    protected var mHasToday = true
    protected var mIsPrev = false
    protected var mSelectedBeginDay = -1
    protected var mSelectedBeginMonth = -1
    protected var mSelectedBeginYear = -1
    protected var mToday = -1
    protected var mWeekStart = 1
    protected var mNumDays = 7
    protected var mNumCells = mNumDays
    private var mDayOfWeekStart = 0
    protected var mMonth: Int = 0
    protected var mRowHeight = DEFAULT_HEIGHT
    protected var mWidth: Int = 0
    protected var mYear: Int = 0
    val today: Time

    private val mCalendar: Calendar
    private val mDayLabelCalendar: Calendar
    private val isPrevDayEnabled: Boolean?

    private var mNumRows = DEFAULT_NUM_ROWS

    private val mDateFormatSymbols = DateFormatSymbols()

    private var mOnDayClickListener: OnDayClickListener? = null

    private val monthAndYearString: String
        get() {
            val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_NO_MONTH_DAY
            val millis = mCalendar.timeInMillis
            return DateUtils.formatDateRange(context, millis, millis, flags)
        }

    init {

        val resources = context.resources
        mDayLabelCalendar = Calendar.getInstance()
        mCalendar = Calendar.getInstance()
        today = Time(Time.getCurrentTimezone())
        today.setToNow()

        MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDay, resources.getDimensionPixelSize(R.dimen.text_size_day))
        MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeMonth, resources.getDimensionPixelSize(R.dimen.text_size_month))
        MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDayName, resources.getDimensionPixelSize(R.dimen.text_size_day_name))
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight, resources.getDimensionPixelOffset(R.dimen.header_month_height))
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius, resources.getDimensionPixelOffset(R.dimen.selected_day_radius))

        mRowHeight = (typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight, resources.getDimensionPixelOffset(R.dimen.calendar_height)) - MONTH_HEADER_SIZE) / 6

        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enablePreviousDay, true)

    }

    private fun calculateNumRows(): Int {
        val offset = findDayOffset()
        val dividend = (offset + mNumCells) / mNumDays
        val remainder = (offset + mNumCells) % mNumDays
        return dividend + if (remainder > 0) 1 else 0
    }

    private fun drawMonthDayLabels(canvas: Canvas) {
        val y = MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE / 2
        val dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2)

        for (i in 0 until mNumDays) {
            val calendarDay = (i + mWeekStart) % mNumDays
            val x = (2 * i + 1) * dayWidthHalf + mPadding
            mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay)

            canvas.drawText(mDateFormatSymbols.shortWeekdays[mDayLabelCalendar.get(Calendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault()), x.toFloat(), y.toFloat(), Paints.dayLabelPaint(context))
        }
    }

    private fun drawMonthTitle(canvas: Canvas) {
        val x = (mWidth + 2 * mPadding) / 2
        val y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + MONTH_LABEL_TEXT_SIZE / 3

        val stringBuilder = StringBuilder(monthAndYearString.toLowerCase())
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.get(0)))
        canvas.drawText(stringBuilder.toString(), 56f, y.toFloat(), Paints.monthTitle(context))
    }

    private fun findDayOffset(): Int {
        return (if (mDayOfWeekStart < mWeekStart) mDayOfWeekStart + mNumDays else mDayOfWeekStart) - mWeekStart
    }

    private fun onDayClick(calendarDay: CalendarAdapter.CalendarDay) {
        if (mOnDayClickListener != null && (isPrevDayEnabled!! || !(calendarDay.month == today.month && calendarDay.year == today.year && calendarDay.day < today.monthDay))) {
            mOnDayClickListener!!.onDayClick(this, calendarDay)
        }
    }

    private fun sameDay(monthDay: Int, time: Time): Boolean {
        return mYear == time.year && mMonth == time.month && monthDay == time.monthDay
    }

    private fun prevDay(monthDay: Int, time: Time): Boolean {
        return mYear < time.year || mYear == time.year && mMonth < time.month || mMonth == time.month && monthDay < time.monthDay
    }

    protected fun drawMonthNums(canvas: Canvas) {
        var y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE
        val paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays)
        var dayOffset = findDayOffset()
        var day = 1

        while (day <= mNumCells) {
            val x = paddingDay * (1 + dayOffset * 2) + mPadding
            if (mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) {
                canvas.drawCircle(x.toFloat(), (y - MINI_DAY_NUMBER_TEXT_SIZE / 3).toFloat(), DAY_SELECTED_CIRCLE_SIZE.toFloat(), Paints.selectedDay(context))
            } else if (mToday == day) {
                canvas.drawCircle(x.toFloat(), (y - MINI_DAY_NUMBER_TEXT_SIZE / 3).toFloat(), DAY_SELECTED_CIRCLE_SIZE.toFloat(), Paints.todayPaint(context))
            }

            val paint = if (mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) Paints.selectedDayLabel(context) else Paints.monthNumPaint(context)

            canvas.drawText(String.format("%d", day), x.toFloat(), y.toFloat(), paint)

            dayOffset++
            if (dayOffset == mNumDays) {
                dayOffset = 0
                y += mRowHeight
            }
            day++
        }
    }

    fun getDayFromLocation(x: Float, y: Float): CalendarAdapter.CalendarDay? {
        val padding = mPadding
        if (x < padding || x > mWidth - mPadding) {
            return null
        }

        val yDay = (y - MONTH_HEADER_SIZE).toInt() / mRowHeight
        val day = 1 + (((x - padding) * mNumDays / (mWidth - padding - mPadding)).toInt() - findDayOffset()) + yDay * mNumDays

        return if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1) null else CalendarAdapter.CalendarDay(mYear, mMonth, day)

    }

    override fun onDraw(canvas: Canvas) {
        drawMonthTitle(canvas)
        drawMonthDayLabels(canvas)
        drawMonthNums(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows + MONTH_HEADER_SIZE)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val calendarDay = getDayFromLocation(event.x, event.y)
            if (calendarDay != null) {
                onDayClick(calendarDay)
            }
        }
        return true
    }

    fun reuse() {
        mNumRows = DEFAULT_NUM_ROWS
        requestLayout()
    }

    fun setMonthParams(params: HashMap<String, Int>) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw InvalidParameterException("You must specify month and year for this view")
        }
        tag = params

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params[VIEW_PARAMS_HEIGHT]!!
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params[VIEW_PARAMS_SELECTED_BEGIN_DAY]!!
        }

        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params[VIEW_PARAMS_SELECTED_BEGIN_MONTH]!!
        }

        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params[VIEW_PARAMS_SELECTED_BEGIN_YEAR]!!
        }
        mMonth = params[VIEW_PARAMS_MONTH]!!
        mYear = params[VIEW_PARAMS_YEAR]!!

        mHasToday = false
        mToday = -1

        mCalendar.set(Calendar.MONTH, mMonth)
        mCalendar.set(Calendar.YEAR, mYear)
        mCalendar.set(Calendar.DAY_OF_MONTH, 1)
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK)

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params[VIEW_PARAMS_WEEK_START]!!
        } else {
            mWeekStart = mCalendar.firstDayOfWeek
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear)
        for (i in 0 until mNumCells) {
            val day = i + 1
            if (sameDay(day, today)) {
                mHasToday = true
                mToday = day
            }

            mIsPrev = prevDay(day, today)
        }

        mNumRows = calculateNumRows()
    }

    fun setOnDayClickListener(onDayClickListener: OnDayClickListener) {
        mOnDayClickListener = onDayClickListener
    }

    interface OnDayClickListener {
        fun onDayClick(simpleMonthView: SimpleMonthView, calendarDay: CalendarAdapter.CalendarDay)
    }

    companion object {

        val VIEW_PARAMS_HEIGHT = "height"
        val VIEW_PARAMS_MONTH = "month"
        val VIEW_PARAMS_YEAR = "year"
        val VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day"
        val VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month"
        val VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year"
        val VIEW_PARAMS_WEEK_START = "week_start"

        protected var DEFAULT_HEIGHT = 32
        protected val DEFAULT_NUM_ROWS = 6
        protected var DAY_SELECTED_CIRCLE_SIZE: Int = 0
        protected var DAY_SEPARATOR_WIDTH = 1
        protected var MINI_DAY_NUMBER_TEXT_SIZE: Int = 0
        protected var MIN_HEIGHT = 10
        protected var MONTH_DAY_LABEL_TEXT_SIZE: Int = 0
        protected var MONTH_HEADER_SIZE: Int = 0
        protected var MONTH_LABEL_TEXT_SIZE: Int = 0
    }
}