package com.example.dailyday.entry

class EntrySleep : EntryValue<Double> {
    constructor() {}

    constructor(value: Double) : super(value, 0.0, 24.0) {}
}