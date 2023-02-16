package com.example.dailyday.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dailyday.R
import com.example.dailyday.database.DatabaseEntries
import com.example.dailyday.entry.Entry
import com.example.dailyday.entry.EntryDate
import com.example.dailyday.entry.EntryScore
import com.example.dailyday.entry.EntrySleep
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ImportActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        backLogic()
        browseLogic()
    }

    /**
     * Implements the back logic
     */
    private fun backLogic() {
        val backButton = findViewById<Button>(R.id.import_back_button)
        backButton.setOnClickListener {
            goToActivity(MainActivity::class.java)
        }
    }

    private fun isCsvFile(data: Intent?): Boolean {
        return data?.data?.path?.endsWith(".csv") ?: return false
    }

    private fun getFileContent(data: Intent?): String {
        val res = contentResolver.openInputStream(data?.data!!) ?: return ""
        val content = res.bufferedReader().use { it.readText() }
        res.close()

        return content
    }

    private val COLUMN_NAMES = listOf(
        "",
        "Appreciation",
        "Hours of sleep",
        "Energy",
        "Activity",
        "Social interaction",
        "Stress",
        "Cry",
        "Outside",
        "Alcohol",
        "Important",
        "Cry Reason"
    )
    private val NUM_COLUMNS = COLUMN_NAMES.size

    private fun isValidCsv(content: String): Boolean {
        val firstLine = content.split("\n")[0]
        val fileColumns = firstLine.split(",").map { it.trim() }
        if (fileColumns.size != NUM_COLUMNS) {
            val toast = Toast.makeText(this, "The given file has an invalid number of columns: ${fileColumns.size}", Toast.LENGTH_SHORT)
            toast.show()
            return false
        }

        for (i in 0 until NUM_COLUMNS) {
            if (fileColumns[i] != COLUMN_NAMES[i]) {
                val toast = Toast.makeText(this, "The given file has an invalid column name: ${fileColumns[i]}", Toast.LENGTH_SHORT)
                toast.show()
                return false
            }
        }

        return true
    }

    val FR_DAYS = listOf(
        "lundi",
        "mardi",
        "mercredi",
        "jeudi",
        "vendredi",
        "samedi",
        "dimanche"
    )

    val FR_MONTHS = listOf(
        "janvier",
        "février",
        "mars",
        "avril",
        "mai",
        "juin",
        "juillet",
        "août",
        "septembre",
        "octobre",
        "novembre",
        "décembre"
    )

    /**
     * Checks basic date formatting and validity
     */
    private fun isValidStrDate(str: String): Boolean {
        val parts = str.trim().split(" ")
        if (parts.size != 4) {
            return false
        }

        val dayOfWeek = parts[0]
        val day = parts[1]
        val month = parts[2]
        val year = parts[3]

        if (!FR_DAYS.contains(dayOfWeek)) {
            return false
        }


        if (!FR_MONTHS.contains(month)) {
            return false
        }

        // Checks if valid entry date, not in the future
        try {
            val entryDate = EntryDate(year.toInt(), FR_MONTHS.indexOf(month) + 1, day.toInt())
            if (entryDate.isinFuture()) {
                return false
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }

    private fun stringToDate(str: String): EntryDate? {
        val newStr = str.replace("\"", "")
        if (!isValidStrDate(newStr)) {
            return null
        }

        val parts = newStr.split(" ")
        val day = parts[1]
        val month = parts[2]
        val year = parts[3]

        return EntryDate(year.toInt(), FR_MONTHS.indexOf(month) + 1, day.toInt())
    }

    private fun stringToScore(str: String): EntryScore? {
        val score = str.replace("\"", "").toIntOrNull() ?: return null
        return EntryScore(score)
    }

    private fun stringToSleep(str: String): EntrySleep? {
        val sleep = str.replace("\"", "").toDoubleOrNull() ?: return null
        return EntrySleep(sleep)
    }

    private fun stringToBoolean(str: String): Boolean {
        return str.replace("\"", "").toBoolean()
    }

    private fun addContentToDatabase(content: String, evaluatedDates: List<EntryDate>) {
        // Remove first line
        val lines = content.split("\n").drop(1)
        for (line in lines) {
            // Define parameters
            val values = line.split(",").map { it.trim() }
            val appreciation = stringToScore(values[1])
            val energy = stringToScore(values[3])
            val activity = stringToScore(values[4])
            val socialInteraction = stringToScore(values[5])
            val stress = stringToScore(values[6])
            val hoursOfSleep = stringToSleep(values[2])
            val cry = stringToBoolean(values[7])
            val outside = stringToBoolean(values[8])
            val alcohol = stringToBoolean(values[9])
            val important = stringToBoolean(values[10])
            val cryReason = values[11].replace("\"", "")
            val date = stringToDate(values[0]) ?: continue

            // Check if date already exists, if so, skip
            if (evaluatedDates.contains(date)) {
                continue
            }

            // Create entry
            val entry = Entry(
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

            // Add to database
            DatabaseEntries.createLoggedInUserEntryToDatabase(auth, Firebase.database, entry)
        }
    }

    /**
     * The file launcher
     */
    private val chooseFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result of the file chooser activity
            val data: Intent? = result.data
            // Check that csv
            if (!isCsvFile(data)) {
                val toast = Toast.makeText(this, "Please choose a csv file", Toast.LENGTH_SHORT)
                toast.show()
                return@registerForActivityResult
            }

            // Get content
            val content = getFileContent(data)
            if (content == "") {
                val toast = Toast.makeText(this, "The given file is empty", Toast.LENGTH_SHORT)
                toast.show()
                return@registerForActivityResult
            }

            // Check valid csv
            if (!isValidCsv(content)) {
                return@registerForActivityResult
            }

            // Add data to database
            DatabaseEntries.getLoggedInUserEvaluatedDates(auth, Firebase.database) { evaluatedDates ->
                addContentToDatabase(content, evaluatedDates)
                val toast = Toast.makeText(this, "The data has been imported", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    /**
     * Implements the browse logic
     */
    private fun browseLogic() {
        val browseButton = findViewById<Button>(R.id.import_browse_button)
        browseButton.setOnClickListener {
            val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
            chooseFile.type = "*/*"

            val intent: Intent = Intent.createChooser(chooseFile, "Choose a file")
            chooseFileLauncher.launch(intent)
        }
    }
}