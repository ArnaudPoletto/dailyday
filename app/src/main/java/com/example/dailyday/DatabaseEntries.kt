package com.example.dailyday

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class DatabaseEntries {
    companion object {
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
                    val entry = entry_data.getValue(Entry::class.java)!!
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
                    val entry = entry_data.getValue(Entry::class.java)!!
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