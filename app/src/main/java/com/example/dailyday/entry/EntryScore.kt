package com.example.dailyday.entry

class EntryScore : EntryValue<Int> {
    constructor() {}

    constructor(value: Int) : super(value, -10, 10) {}
}