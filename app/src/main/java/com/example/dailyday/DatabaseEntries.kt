package com.example.dailyday

import com.example.dailyday.entry.Entry
import com.example.dailyday.entry.EntryDate
import com.example.dailyday.entry.EntryScore
import com.example.dailyday.entry.EntrySleep
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class DatabaseEntries {
    companion object {

        private fun entryDataToEntry(entryData: Any?): Entry {
            // Entry data must not be null
            if (entryData == null) {
                throw IllegalArgumentException("Entry data must not be null")
            }

            // Entry data must be a map
            try {
                var entryDataMap = entryData as Map<*, *>
            } catch (e: ClassCastException) {
                throw IllegalArgumentException("Entry data must be a map")
            }

            // Get data from map
            val appreciation: EntryScore?
            if (entryData.containsKey("appreciation")) {
                // appreciation -> value
                appreciation =  EntryScore(((entryData["appreciation"] as Map<*, *>)["value"] as Long).toInt())
            } else {
                appreciation = null
            }

            val energy: EntryScore?
            if (entryData.containsKey("energy")) {
                energy = EntryScore(((entryData["energy"] as Map<*, *>)["value"] as Long).toInt())
            } else {
                energy = null
            }

            var hoursOfSleep: EntrySleep?
            if (entryData.containsKey("hoursOfSleep")) {
                // Try casting as Long and then as Double
                try {
                    val hoursOfSleepLong = (entryData["hoursOfSleep"] as Map<*, *>)["value"] as Long
                    hoursOfSleep = EntrySleep(hoursOfSleepLong.toDouble())
                } catch (e: ClassCastException) {
                    hoursOfSleep = EntrySleep((entryData["hoursOfSleep"] as Map<*, *>)["value"] as Double)
                }
            } else {
                hoursOfSleep = null
            }

            val dateYear: Int = ((entryData["date"] as Map<*, *>)["year"] as Long).toInt()
            val dateMonth: Int = ((entryData["date"] as Map<*, *>)["month"] as Long).toInt()
            val dateDay: Int = ((entryData["date"] as Map<*, *>)["day"] as Long).toInt()
            val date = EntryDate(dateYear, dateMonth, dateDay)

            return Entry(appreciation, energy, hoursOfSleep, date)
        }

        /**
         * Returns the path to the entries of the logged in user.
         */
        private fun getLoggedInUserEntriesPath(auth: FirebaseAuth): String {
            return "user_data/${auth.currentUser?.uid}/entries"
        }

        /**
         * Returns a list of all entries of the logged in user.
         */
       fun getLoggedInUserEntries(auth: FirebaseAuth, database: FirebaseDatabase, callback: (List<Entry>) -> Unit) {
            val entries = database.getReference(getLoggedInUserEntriesPath(auth))
            entries.get().addOnSuccessListener {
                val entriesList = mutableListOf<Entry>()
                for (entry_data in it.children) {
                    val entry = entryDataToEntry(entry_data.value)
                    entriesList.add(entry)
                }
                callback(entriesList)
            }
       }

        /**
         * Returns the entry of the logged in user for today.
         */
        fun getLoggedInUserTodayEntry(auth: FirebaseAuth, database: FirebaseDatabase, callback: (Entry?) -> Unit) {
            getLoggedInUserEntries(auth, database) { entries ->
                for (entry in entries) {
                    val entryDate: EntryDate = entry.getDate()
                    if (entryDate == EntryDate.today()) {
                        callback(entry)
                        return@getLoggedInUserEntries
                    }
                }
                callback(null)
            }
        }

        /**
         * Returns true if the logged in user has already evaluated today.
         */
        fun loggedInUserAlreadyEvaluatedToday(auth: FirebaseAuth, database: FirebaseDatabase, callback: (Boolean) -> Unit) {
            getLoggedInUserTodayEntry(auth, database) { entry ->
                if (entry != null) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }

        /**
         * Creates a new entry for the logged in user.
         */
        fun createLoggedInUserEntryToDatabase(auth: FirebaseAuth, database: FirebaseDatabase, entry: Entry) {
            val reference = database.getReference(getLoggedInUserEntriesPath(auth))
            reference.push().setValue(entry)
        }

        /**
         * Edits an entry of the logged in user.
         */
        fun editLoggedInUserEntryToDatabase(auth: FirebaseAuth, database: FirebaseDatabase, modifiedEntry: Entry) {
            val entries = database.getReference(getLoggedInUserEntriesPath(auth))
            entries.get().addOnSuccessListener {
                for (entry_data in it.children) {
                    val entry = entryDataToEntry(entry_data.value)
                    val entryDate: EntryDate = entry.getDate()
                    if (entryDate == modifiedEntry.getDate()) {
                        modifiedEntry.setUpdatedAt(Calendar.getInstance().timeInMillis)
                        entry_data.ref.setValue(modifiedEntry)
                    }
                }
            }
        }
    }
}