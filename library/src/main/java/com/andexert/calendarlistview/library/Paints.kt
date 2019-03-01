package com.andexert.calendarlistview.library

import android.content.Context
import android.graphics.Paint

object Paints {

    fun monthTitle(context: Context): Paint {
        return Paint().apply {
            isFakeBoldText = true
            isAntiAlias = true
            color = context.getColor(R.color.normal_day)
            textAlign = Paint.Align.LEFT
            style = Paint.Style.FILL
            textSize = context.resources.getDimensionPixelOffset(R.dimen.text_size_day).toFloat()
        }
    }

    fun selectedDay(context: Context): Paint {
        return Paint().apply {
            isFakeBoldText = true
            isAntiAlias = true
            color = context.getColor(R.color.selected_day_background)
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            textSize = context.resources.getDimensionPixelOffset(R.dimen.text_size_day).toFloat()
        }
    }

    fun selectedDayLabel(context: Context): Paint {
        return Paint().apply {
            isFakeBoldText = true
            isAntiAlias = true
            color = context.getColor(R.color.selected_day_text)
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            textSize = context.resources.getDimensionPixelOffset(R.dimen.text_size_day).toFloat()
        }
    }

    fun todayPaint(context: Context): Paint {
        return Paint().apply {
            isFakeBoldText = true
            isAntiAlias = true
            color = context.getColor(R.color.selected_day_background)
            textAlign = Paint.Align.CENTER
            style = Paint.Style.STROKE
            strokeWidth = 8.0f
            textSize = context.resources.getDimensionPixelOffset(R.dimen.text_size_day).toFloat()
        }
    }

    fun dayLabelPaint(context: Context): Paint {
        return Paint().apply {
            isAntiAlias = true
            textSize = context.resources.getDimensionPixelSize(R.dimen.text_size_day_name).toFloat()
            color = context.getColor(R.color.normal_day)
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
    }

    fun monthNumPaint(context: Context): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            isFakeBoldText = false
            textSize = context.resources.getDimensionPixelOffset(R.dimen.text_size_day).toFloat()
        }
    }
}