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
 ***********************************************************************************/
package com.andexert.calendarlistview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;

import java.util.Calendar;
import java.util.HashMap;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> implements SimpleMonthView.OnDayClickListener {
    private static final int MONTHS_IN_YEAR = 12;
    private final TypedArray typedArray;
    private final Context mContext;
    private final DatePickerController mController;
    private final Calendar calendar;
    private CalendarDay selectedDays;
    private CalendarDay dueDate;
    private final Integer firstMonth;
    private final Integer lastMonth;

    CalendarAdapter(Context context, DatePickerController datePickerController, TypedArray typedArray) {
        this.typedArray = typedArray;
        calendar = Calendar.getInstance();
        firstMonth = typedArray.getInt(R.styleable.DayPickerView_firstMonth, calendar.get(Calendar.MONTH));
        lastMonth = typedArray.getInt(R.styleable.DayPickerView_lastMonth, (calendar.get(Calendar.MONTH) - 1) % MONTHS_IN_YEAR);
        mContext = context;
        mController = datePickerController;
        init();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final SimpleMonthView simpleMonthView = new SimpleMonthView(mContext, typedArray);
        return new ViewHolder(simpleMonthView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final SimpleMonthView v = viewHolder.simpleMonthView;
        final HashMap<String, Integer> drawingParams = new HashMap<String, Integer>();
        int month;
        int year;

        month = (firstMonth + (position % MONTHS_IN_YEAR)) % MONTHS_IN_YEAR;
        year = position / MONTHS_IN_YEAR + calendar.get(Calendar.YEAR) + ((firstMonth + (position % MONTHS_IN_YEAR)) / MONTHS_IN_YEAR);

        int selectedFirstDay = -1;
        int selectedFirstMonth = -1;
        int selectedFirstYear = -1;

        int dueDateDay = -1;
        int dueDateMonth = -1;
        int dueDateYear = -1;
        if (selectedDays != null) {
            selectedFirstDay = selectedDays.day;
            selectedFirstMonth = selectedDays.month;
            selectedFirstYear = selectedDays.year;
        }

        if (dueDate != null) {
            dueDateDay = dueDate.day;
            dueDateMonth = dueDate.month;
            dueDateYear = dueDate.year;
        }

        v.reuse();

        drawingParams.put(SimpleMonthView.Companion.getVIEW_PARAMS_SELECTED_BEGIN_YEAR(), selectedFirstYear);
        drawingParams.put(SimpleMonthView.Companion.getVIEW_PARAMS_SELECTED_BEGIN_MONTH(), selectedFirstMonth);
        drawingParams.put(SimpleMonthView.Companion.getVIEW_PARAMS_SELECTED_BEGIN_DAY(), selectedFirstDay);
        drawingParams.put(SimpleMonthView.Companion.getVIEW_PARAMS_YEAR(), year);
        drawingParams.put(SimpleMonthView.Companion.getVIEW_PARAMS_MONTH(), month);
        drawingParams.put(SimpleMonthView.Companion.getVIEW_PARAMS_WEEK_START(), calendar.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.invalidate();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = (((mController.getMaxYear() - calendar.get(Calendar.YEAR)) + 1) * MONTHS_IN_YEAR);

        if (firstMonth != -1)
            itemCount -= firstMonth;

        if (lastMonth != -1)
            itemCount -= (MONTHS_IN_YEAR - lastMonth) - 1;

        return itemCount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final SimpleMonthView simpleMonthView;

        ViewHolder(View itemView, SimpleMonthView.OnDayClickListener onDayClickListener) {
            super(itemView);
            simpleMonthView = (SimpleMonthView) itemView;
            simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            simpleMonthView.setClickable(true);
            simpleMonthView.setOnDayClickListener(onDayClickListener);
        }
    }

    private void init() {
        if (typedArray.getBoolean(R.styleable.DayPickerView_currentDaySelected, false))
            onDayTapped(new CalendarDay(System.currentTimeMillis()));
    }

    public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
        if (calendarDay != null) {
            onDayTapped(calendarDay);
        }
    }

    private void onDayTapped(CalendarDay calendarDay) {
        mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
        setSelectedDay(calendarDay);
    }

    private void setSelectedDay(CalendarDay calendarDay) {

        selectedDays = calendarDay;

        notifyDataSetChanged();
    }

    static class CalendarDay {
        private Calendar calendar;

        int day;
        int month;
        int year;

        CalendarDay(int year, int month, int day) {
            setDay(year, month, day);
        }

        CalendarDay(long timeInMillis) {
            setTime(timeInMillis);
        }

        private void setTime(long timeInMillis) {
            if (calendar == null) {
                calendar = Calendar.getInstance();
            }
            calendar.setTimeInMillis(timeInMillis);
            month = this.calendar.get(Calendar.MONTH);
            year = this.calendar.get(Calendar.YEAR);
            day = this.calendar.get(Calendar.DAY_OF_MONTH);
        }

        void setDay(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }
}