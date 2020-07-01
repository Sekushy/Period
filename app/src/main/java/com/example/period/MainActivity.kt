package com.example.period

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        password = findViewById(R.id.password_input)
        email = findViewById(R.id.email_input)

        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.log_in_btn).setOnClickListener {
            loginButtonIsPressed()
        }
    }

    fun validateForm(): Boolean {
        var valid = true

        val email = findViewById<EditText>(R.id.email_input).text.toString()
        if (TextUtils.isEmpty(email)) {
            findViewById<EditText>(R.id.email_input).error = "Required."
            valid = false
        } else {
            findViewById<EditText>(R.id.email_input).error = null
        }

        val password = findViewById<EditText>(R.id.password_input).text.toString()
        if (TextUtils.isEmpty(password)) {
            findViewById<EditText>(R.id.password_input).error = "Required."
            valid = false
        } else {
            findViewById<EditText>(R.id.password_input).error = null
        }
        return valid
    }

    fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this!!) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun loginButtonIsPressed() {
        if (password.text.toString() != "" && email.text.toString() != "") {
            signIn(email.text.toString(), password.text.toString())
        } else {
            Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_LONG).show()
        }
    }

    fun signUpRequest(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }


}
