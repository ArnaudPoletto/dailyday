package com.example.dailyday

import java.util.Date

class Entry : java.io.Serializable{
    private var timestamp: Long? = null
    private var appreciation: Int? = null
    private var hoursOfSleep: Double? = null
    private var energy: Int? = null


    constructor() { }

    constructor(appreciation: Int?, hoursOfSleep: Double?, energy: Int?) {
        if (appreciation != null && (appreciation < -10 || appreciation > 10)) {
            throw IllegalArgumentException("Appreciation must be between -10 and 10")
        }
        if (hoursOfSleep != null && (hoursOfSleep < 0 || hoursOfSleep > 24)) {
            throw IllegalArgumentException("Hours of sleep must be between 0 and 24")
        }
        if (energy != null && (energy < -10 || energy > 10)) {
            throw IllegalArgumentException("Energy must be between -10 and 10")
        }

        this.timestamp = Date().time

        this.appreciation = appreciation
        this.hoursOfSleep = hoursOfSleep
        this.energy = energy
    }

    fun getTimestamp(): Long {
        if (timestamp == null) {
            timestamp = 0
        }

        return timestamp as Long
    }

    fun getAppreciation(): Int? {
        return appreciation
    }

    fun getHoursOfSleep(): Double? {
        return hoursOfSleep
    }

    fun getEnergy(): Int? {
        return energy
    }
}