package com.hynet.heebit.components.utils

import com.hynet.heebit.components.constant.Regex
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    companion object {

        fun formatToLong(pattern: String, date: String): Long {
            try {
                val calendar = Calendar.getInstance()
                calendar.time = SimpleDateFormat(pattern, Locale.getDefault()).parse(date)
                return calendar.timeInMillis
            } catch (e: ParseException) {
                e.printStackTrace()
                return 0
            }
        }

        fun formatToDate(pattern: String, date: Long): String {
            return dateToString(pattern, Date(date))
        }

        fun dateToString(pattern: String, date: Date): String {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
        }

        fun stringToDate(pattern: String, date: String): Date {
            return SimpleDateFormat(pattern, Locale.getDefault()).parse(date, ParsePosition(0))
        }

        fun geCurrentDate(pattern: String): Date {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            return formatter.parse(formatter.format(Date()), ParsePosition(0))
        }

        fun geCurrentStringDate(pattern: String): String {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
        }

        fun getCurrentTime(date: String, regex: String): String {
            val dateFormat = SimpleDateFormat(regex, Locale.getDefault())
            val parsePosition = ParsePosition(0)
            dateFormat.isLenient = false
            return dateFormat.parse(date, parsePosition).toString()
        }

        fun getTime(id: String): String {
            val dateFormat = SimpleDateFormat(Regex.DATE_FORMAT1.regext, Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone(id)
            return dateFormat.format(Date())
        }

        fun getTimeMillis(time: String): Long {
            try {
                return SimpleDateFormat(Regex.DATE_FORMAT9.regext, Locale.getDefault()).parse(time).time
            } catch (e: ParseException) {
                e.printStackTrace()
                return 0
            }
        }

        fun getUtcTimeMillis(time: String): Long {
            try {
                val simpleDateFormat = SimpleDateFormat(Regex.DATE_FORMAT10.regext)
                simpleDateFormat.timeZone = TimeZone.getTimeZone(Regex.UTC.regext)
                val date = simpleDateFormat.parse(time)
                return date.time
            } catch (e: ParseException) {
                e.printStackTrace()
                return 0
            }
        }

        fun utc2date(time: String): String {
            try {
                val simpleDateFormat = SimpleDateFormat(Regex.DATE_FORMAT10.regext)
                simpleDateFormat.timeZone = TimeZone.getTimeZone(Regex.UTC.regext)
                val date = simpleDateFormat.parse(time)
                simpleDateFormat.applyPattern(Regex.DATE_FORMAT9.regext)
                simpleDateFormat.timeZone = TimeZone.getDefault()
                return simpleDateFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                return time
            }
        }

    }

}