package com.example.dailyday.entry

import java.util.Calendar
import java.util.Date

class Entry : java.io.Serializable{
    private var appreciation: EntryScore? = null
    private var energy: EntryScore? = null
    private var activity: EntryScore? = null
    private var socialInteraction: EntryScore? = null
    private var stress: EntryScore? = null
    private var hoursOfSleep: EntrySleep? = null
    private var cry: Boolean? = null
    private var outside: Boolean? = null
    private var alcohol: Boolean? = null
    private var important: Boolean? = null
    private var cryReason: String? = null
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
        activity: Int?,
        socialInteraction: Int?,
        stress: Int?,
        hoursOfSleep: Double?,
        cry: Boolean?,
        outside: Boolean?,
        alcohol: Boolean?,
        important: Boolean?,
        cryReason: String?,
        date: Calendar
    ) {
        if (appreciation != null) {
            this.appreciation = EntryScore(appreciation)
        }
        if (energy != null) {
            this.energy = EntryScore(energy)
        }
        if (activity != null) {
            this.activity = EntryScore(activity)
        }
        if (socialInteraction != null) {
            this.socialInteraction = EntryScore(socialInteraction)
        }
        if (stress != null) {
            this.stress = EntryScore(stress)
        }
        if (hoursOfSleep != null) {
            this.hoursOfSleep = EntrySleep(hoursOfSleep)
        }
        this.cry = cry
        this.outside = outside
        this.alcohol = alcohol
        this.important = important
        this.cryReason = cryReason
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
        activity: EntryScore?,
        socialInteraction: EntryScore?,
        stress: EntryScore?,
        hoursOfSleep: EntrySleep?,
        cry: Boolean?,
        outside: Boolean?,
        alcohol: Boolean?,
        important: Boolean?,
        cryReason: String?,
        date: EntryDate
    ) {
        this.appreciation = appreciation
        this.energy = energy
        this.activity = activity
        this.socialInteraction = socialInteraction
        this.stress = stress
        this.hoursOfSleep = hoursOfSleep
        this.cry = cry
        this.outside = outside
        this.alcohol = alcohol
        this.important = important
        this.cryReason = cryReason
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
     * get the activity score
     * @return the activity score
     */
    fun getActivity(): EntryScore? {
        if (this.activity == null) {
            return null
        }

        return this.activity!!
    }

    /**
     * get the social interaction score
     * @return the social interaction score
     */
    fun getSocialInteraction(): EntryScore? {
        if (this.socialInteraction == null) {
            return null
        }

        return this.socialInteraction!!
    }

    /**
     * get the stress score
     * @return the stress score
     */
    fun getStress(): EntryScore? {
        if (this.stress == null) {
            return null
        }

        return this.stress!!
    }

    /**
     * get the hours of sleep
     * @return the hours of sleep
     */
    fun getHoursOfSleep(): EntrySleep? {
        return hoursOfSleep
    }

    /**
     * get the cry
     * @return the cry
     */
    fun getCry(): Boolean? {
        return cry
    }

    /**
     * get the outside
     * @return the outside
     */
    fun getOutside(): Boolean? {
        return outside
    }

    /**
     * get the alcohol
     * @return the alcohol
     */
    fun getAlcohol(): Boolean? {
        return alcohol
    }

    /**
     * get the important
     * @return the important
     */
    fun getImportant(): Boolean? {
        return important
    }

    /**
     * get the cry reason
     * @return the cry reason
     */
    fun getCryReason(): String? {
        return cryReason
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