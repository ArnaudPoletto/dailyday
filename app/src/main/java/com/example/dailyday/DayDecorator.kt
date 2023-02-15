package com.example.dailyday

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.dailyday.entry.EntryDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DayDecorator(context: Context) : DayViewDecorator {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var backgroundDrawable: Drawable
    private lateinit var dates : List<EntryDate>

    constructor(auth : FirebaseAuth, database : FirebaseDatabase, dates : List<EntryDate>, context : Context) : this(context) {
        this.auth = auth
        this.database = database
        this.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.green_background)!!
        this.dates = dates
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val entryDate = EntryDate(day.year, day.month, day.day)
        return dates.contains(entryDate)
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(backgroundDrawable)
    }
}