package com.example.dailyday.entry

import java.util.Calendar
import java.util.Date

class Entry : java.io.Serializable{
    private var appreciation: EntryScore? = null
    private var energy: EntryScore? = null
    private var hoursOfSleep: EntrySleep? = null

    private lateinit var date: EntryDate

    private var updatedAt: Long = Date().time
    private var createdAt: Long = Date().time


    constructor() { }

    /**
     * Constructor for creating a new entry from basic data types
     * @param appreciation the appreciation score
     * @param energy the energy score
     * @param hoursOfSleep the hours of sleep
     * @param date the date
     * @throws IllegalArgumentException if the data is invalid
     */
    constructor(
        appreciation: Int?,
        energy: Int?,
        hoursOfSleep: Double?,
        date: Calendar
    ) {
        if (appreciation != null) {
            this.appreciation = EntryScore(appreciation)
        }
        if (energy != null) {
            this.energy = EntryScore(energy)
        }
        if (hoursOfSleep != null) {
            this.hoursOfSleep = EntrySleep(hoursOfSleep)
        }
        this.date = EntryDate(date)
    }

    /**
     * Constructor for creating a new entry from entity data types
     * @param appreciation the appreciation score
     * @param energy the energy score
     * @param hoursOfSleep the hours of sleep
     * @param date the date
     * @throws IllegalArgumentException if the data is invalid
     */
    constructor(
        appreciation: EntryScore?,
        energy: EntryScore?,
        hoursOfSleep: EntrySleep?,
        date: EntryDate
    ) {
        this.appreciation = appreciation
        this.energy = energy
        this.hoursOfSleep = hoursOfSleep
        this.date = date
    }

    /**
     * get the appreciation score
     * @return the appreciation score
     */
    fun getAppreciation(): EntryScore? {
        if (this.appreciation == null) {
            return null
        }

        return this.appreciation!!
    }

    /**
     * get the energy score
     * @return the energy score
     */
    fun getEnergy(): EntryScore? {
        if (this.energy == null) {
            return null
        }

        return this.energy!!
    }

    /**
     * get the hours of sleep
     * @return the hours of sleep
     */
    fun getHoursOfSleep(): EntrySleep? {
        return hoursOfSleep
    }

    /**
     * get the date
     * @return the date
     */
    fun getDate(): EntryDate {
        return date
    }

    /**
     * get the created at timestamp
     * @return the created at timestamp
     */
    fun getCreatedAt(): Long {
        return createdAt
    }

    /**
     * get the updated at timestamp
     * @return the updated at timestamp
     */
    fun getUpdatedAt(): Long {
        return updatedAt
    }

    /**
     * set the updated at timestamp
     * @param updatedAt the updated at timestamp
     */
    fun setUpdatedAt(updatedAt: Long) {
        this.updatedAt = updatedAt
    }
}