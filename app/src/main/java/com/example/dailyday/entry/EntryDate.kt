package com.example.dailyday.entry

import java.util.Calendar
import java.util.Date

class EntryDate {
    private var year: Int = 1970
    private var month: Int = 1 // 1-indexed
    private var day: Int = 1

    constructor() { }

    /**
     * Constructor for creating an entry date from basic data types
     * @param year the year
     * @param month the month
     * @param day the day
     * @throws IllegalArgumentException if the data is invalid
     */
    constructor(year: Int, month: Int, day: Int) {
        checkDate(year, month, day)

        this.year = year
        this.month = month
        this.day = day
    }

    /**
     * Constructor for creating an entry date from a calendar
     * @param calendar the calendar
     */
    constructor(calendar: Calendar) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar months are 0-indexed
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        checkDate(year, month, day)

        this.year = year
        this.month = month
        this.day = day
    }

    private fun checkDate(year: Int, month: Int, day: Int) {
        val calendarDate = Calendar.getInstance()
        calendarDate.isLenient = false // Don't allow invalid dates
        try {
            calendarDate.set(year, month - 1, day) // Calendar months are 0-indexed
            calendarDate.time
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid date: $day/$month/$year")
        }
    }

    /**
     * get the year
     * @return the year
     */
    fun getYear(): Int {
        return year
    }

    /**
     * get the month
     * @return the month
     */
    fun getMonth(): Int {
        return month
    }

    /**
     * get the day
     * @return the day
     */
    fun getDay(): Int {
        return day
    }

    /**
     * get the timestamp
     * @return the timestamp
     */
    fun getTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.timeInMillis
    }

    /**
     * set the timestamp
     * @param timestamp the timestamp to set
     */
    fun setTimestamp(timestamp: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        this.year = calendar.get(Calendar.YEAR)
        this.month = calendar.get(Calendar.MONTH)
        this.day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day) // Calendar months are 0-indexed
        return calendar
    }

    override fun toString(): String {
        return "$day/$month/$year"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other !is EntryDate) {
            return false
        }

        return this.year == other.year
                && this.month == other.month
                && this.day == other.day
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + day
        return result
    }

    companion object {
        /**
         * Get the current date
         */
        fun today(): EntryDate {
            return EntryDate(Calendar.getInstance())
        }

        fun fromString(str: String): EntryDate {
            val parts = str.split("/")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid date string: $str")
            }
            val year = parts[2].toInt()
            val month = parts[1].toInt()
            val day = parts[0].toInt()
            return EntryDate(year, month, day)
        }
    }
}