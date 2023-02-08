package com.example.dailyday

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // register button
        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            register()
        }

        // login button
        val loginButton = findViewById<Button>(R.id.login_register_button)
        loginButton.setOnClickListener {
            login()
        }

    }

    private fun register() {
        val username: String = findViewById<EditText>(R.id.register_field_username).text.toString()
        val email: String = findViewById<EditText>(R.id.register_field_email).text.toString()
        val password: String = findViewById<EditText>(R.id.register_field_password).text.toString()


        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return
        }

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")

                    val userDb = User(username, email, password)
                    val database = Firebase.database
                    val myRef = database.getReference("Users")
                    myRef.child(userDb.getUsername()).setValue(userDb)

                    Toast.makeText(
                        baseContext, "Authentication success.",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}