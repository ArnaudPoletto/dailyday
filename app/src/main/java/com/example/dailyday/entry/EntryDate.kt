package com.example.dailyday.entry

import java.util.Calendar

class EntryDate {
    private var year: Int = 1970
    private var month: Int = 1
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
        if (year < 1970) {
            throw IllegalArgumentException("Year must be greater than 1970")
        }
        if (month < 0 || month > 11) {
            throw IllegalArgumentException("Month must be between 0 and 11")
        }
        if (day < 1 || day > 31) {
            throw IllegalArgumentException("Day must be between 1 and 31")
        }

        this.year = year
        this.month = month
        this.day = day
    }

    /**
     * Constructor for creating an entry date from a calendar
     * @param calendar the calendar
     */
    constructor(calendar: Calendar) {
        this.year = calendar.get(Calendar.YEAR)
        this.month = calendar.get(Calendar.MONTH)
        this.day = calendar.get(Calendar.DAY_OF_MONTH)
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
    }
}