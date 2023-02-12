package com.example.dailyday

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {
    protected lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    /**
     * Logs out the user
     */
    protected fun logout() {
        auth.signOut()
        goToActivity(LoginActivity::class.java)
    }

    /**
     * Go to an activity
     */
    protected fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        finish()
    }
}