package com.example.dailyday.entry

abstract class EntryValue<T: Number> {
    private lateinit var value: T
    private lateinit var min: T
    private lateinit var max: T

    constructor() {}

    constructor(value: T, min: T, max: T) {
        if (value.toDouble() < min.toDouble() || value.toDouble() > max.toDouble()) {
            throw IllegalArgumentException("Value must be between $min and $max")
        }

        this.value = value
        this.min = min
        this.max = max
    }

    /**
     * get the value
     */
    fun getValue(): T {
        return this.value
    }

    /**
     * set the value
     * @param value the value
     */
    fun setValue(value: T) {
        if (value.toDouble() < min.toDouble() || value.toDouble() > max.toDouble()) {
            throw IllegalArgumentException("Value must be between $min and $max")
        }

        this.value = value
    }
}