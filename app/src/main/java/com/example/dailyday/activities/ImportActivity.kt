package com.example.dailyday.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.dailyday.R

class ImportActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        backLogic()
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
}