package com.example.dailyday.database

import com.example.dailyday.activities.HistoryActivity
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

            val activity: EntryScore?
            if (entryData.containsKey("activity")) {
                activity = EntryScore(((entryData["activity"] as Map<*, *>)["value"] as Long).toInt())
            } else {
                activity = null
            }

            val socialInteraction: EntryScore?
            if (entryData.containsKey("socialInteraction")) {
                socialInteraction = EntryScore(((entryData["socialInteraction"] as Map<*, *>)["value"] as Long).toInt())
            } else {
                socialInteraction = null
            }

            val stress: EntryScore?
            if (entryData.containsKey("stress")) {
                stress = EntryScore(((entryData["stress"] as Map<*, *>)["value"] as Long).toInt())
            } else {
                stress = null
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

            var cry: Boolean?
            if (entryData.containsKey("cry")) {
                cry = (entryData["cry"] as Boolean)
            } else {
                cry = null
            }

            var outside: Boolean?
            if (entryData.containsKey("outside")) {
                outside = (entryData["outside"] as Boolean)
            } else {
                outside = null
            }

            var alcohol: Boolean?
            if (entryData.containsKey("alcohol")) {
                alcohol = (entryData["alcohol"] as Boolean)
            } else {
                alcohol = null
            }

            var important: Boolean?
            if (entryData.containsKey("important")) {
                important = (entryData["important"] as Boolean)
            } else {
                important = null
            }

            var cryReason: String?
            if (entryData.containsKey("cryReason")) {
                cryReason = (entryData["cryReason"] as String)
            } else {
                cryReason = null
            }

            val dateYear: Int = ((entryData["date"] as Map<*, *>)["year"] as Long).toInt()
            val dateMonth: Int = ((entryData["date"] as Map<*, *>)["month"] as Long).toInt()
            val dateDay: Int = ((entryData["date"] as Map<*, *>)["day"] as Long).toInt()
            val date = EntryDate(dateYear, dateMonth, dateDay)

            return Entry(
                appreciation,
                energy,
                activity,
                socialInteraction,
                stress,
                hoursOfSleep,
                cry,
                outside,
                alcohol,
                important,
                cryReason,
                date
            )
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
        fun getLoggedInUserEntryAtDate(auth: FirebaseAuth, database: FirebaseDatabase, date: EntryDate, callback: (Entry?) -> Unit) {
            getLoggedInUserEntries(auth, database) { entries ->
                for (entry in entries) {
                    val entryDate: EntryDate = entry.getDate()
                    if (entryDate == date) {
                        callback(entry)
                        return@getLoggedInUserEntries
                    }
                }
                callback(null)
            }
        }

        fun getLoggedInUserEvaluatedDates(
            auth: FirebaseAuth,
            database: FirebaseDatabase,
            callback: (List<EntryDate>) -> Unit
        ) {
            val dates = mutableListOf<EntryDate>()
            getLoggedInUserEntries(auth, database) { entries ->
                for (entry in entries) {
                    val date: EntryDate = entry.getDate()
                    dates.add(date)
                }
                callback(dates)
            }
        }

        /**
         * Returns true if the logged in user has already evaluated today.
         */
        fun loggedInUserAlreadyEvaluatedToday(auth: FirebaseAuth, database: FirebaseDatabase, callback: (Boolean) -> Unit) {
            val today = EntryDate(Calendar.getInstance())
            loggedInUserAlreadyEvaluatedAtDate(auth, database, today, callback)
        }

        fun loggedInUserAlreadyEvaluatedAtDate(
            auth: FirebaseAuth,
            database: FirebaseDatabase,
            date: EntryDate,
            callback: (Boolean) -> Unit
        ) {
            getLoggedInUserEntries(auth, database) { entries ->
                for (entry in entries) {
                    val entryDate: EntryDate = entry.getDate()
                    if (entryDate == date) {
                        callback(true)
                        return@getLoggedInUserEntries
                    }
                }
                callback(false)
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