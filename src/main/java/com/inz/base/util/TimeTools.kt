package com.inz.base.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * Time Tools .
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object TimeTools {


    const val LONG_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val BASE_TIME_MILL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    const val BASE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DATE_TIME_24_FORMAT = "HH:mm:ss"
    const val DATE_TIME_12_FORMAT = "hh:mm:ss a"


    /**
     * Get Time String
     */
    fun getTimeString(date: Date, timeFormat: DateFormat): String {
        return timeFormat.format(date)
    }


    /**
     * Get Time String .
     */
    fun getTimeString(
        date: Date = Calendar.getInstance(Locale.getDefault()).time,
        timeFormatStr: String = BASE_TIME_FORMAT
    ): String {
        return getTimeString(date, getTimeFormat(timeFormatStr))
    }

    /**
     * Get Time Format
     */
    fun getTimeFormat(timeFormatStr: String): DateFormat {
        return SimpleDateFormat(timeFormatStr, Locale.getDefault())
    }

    /**
     * Get Current Day Zero time 00:00:00.000
     */
    fun getDayZeroTime(calendar: Calendar = Calendar.getInstance(Locale.getDefault())): Calendar {
        val targetCalendar = calendar.clone() as Calendar
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0)
        targetCalendar.set(Calendar.MINUTE, 0)
        targetCalendar.set(Calendar.SECOND, 0)
        targetCalendar.set(Calendar.MILLISECOND, 0)
        return targetCalendar
    }

    /**
     * Get Current Day Last time  23:59:59:999
     */
    fun getDayLastTime(calendar: Calendar = Calendar.getInstance(Locale.getDefault())): Calendar {
        var targetCalendar = calendar.clone() as Calendar
        targetCalendar.add(Calendar.DAY_OF_YEAR, 1)
        targetCalendar = getDayZeroTime(targetCalendar)
        targetCalendar.add(Calendar.MILLISECOND, -1)
        return targetCalendar
    }

    /**
     * Get Current Month Zero time 2.1 00:00:00.000
     */
    fun getMonthZeroTime(calendar: Calendar = Calendar.getInstance(Locale.getDefault())): Calendar {
        var targetCalendar = calendar.clone() as Calendar
        targetCalendar.set(Calendar.DAY_OF_MONTH, 1)
        targetCalendar = getDayZeroTime(targetCalendar)
        return targetCalendar
    }

    /**
     * get Time Gap Day
     */
    fun getTimeGapDay(firstCalendar: Calendar, secondCalendar: Calendar): Int {
        val difMill = getTimeGapMill(firstCalendar, secondCalendar)
        val dayTime = 1000 * 60 * 60 * 24
        var difDay = difMill.div(dayTime)
        val difHour = difMill.rem(dayTime)
        if (difHour > 0) {
            difDay += 1
        }
        return difDay
    }


    /**
     * get Time Gap Mill
     */
    fun getTimeGapMill(firstCalendar: Calendar, secondCalendar: Calendar): Int {
        var difMill = firstCalendar.get(Calendar.MILLISECOND) -
                secondCalendar.get(Calendar.MILLISECOND)
        if (firstCalendar.before(secondCalendar)) {
            difMill = -difMill
        }
        return difMill
    }

    /**
     * get Time Gap Month
     */
    fun getTimeGapMonth(firstCalendar: Calendar, secondCalendar: Calendar): Int {
        val difYear = firstCalendar.get(Calendar.YEAR) - secondCalendar.get(Calendar.YEAR)
        var difMonth = firstCalendar.get(Calendar.MONTH) - secondCalendar.get(Calendar.MONTH)
        val difDay = firstCalendar.get(Calendar.DAY_OF_MONTH) -
                secondCalendar.get(Calendar.DAY_OF_MONTH)
        difMonth = abs(difYear) * 12 + abs(difMonth)
        if (firstCalendar.before(secondCalendar) && difDay < 0) {
            difMonth += 1
        }
        return difMonth
    }


}