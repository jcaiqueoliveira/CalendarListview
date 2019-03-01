package com.andexert.calendarlistview.library

sealed class CalendarType {
    object InternalTransfer : CalendarType()
    class TED(val weekendAndHolliday: String)
    class Payment(val dueDate: String)
}