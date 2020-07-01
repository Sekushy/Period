package com.example.period

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    lateinit var username : EditText
    lateinit var password : EditText
    lateinit var email : EditText
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        username = findViewById(R.id.username_input)
        password = findViewById(R.id.password_input)
        email = findViewById(R.id.email_input)

        auth = FirebaseAuth.getInstance()
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
                    val user = auth.currentUser

                    val currentUserUID = auth.currentUser!!.uid
//                    userToReturn.displayName = FirebaseAuth.getInstance().currentUser!!.displayName!!
//                    userToReturn.email = FirebaseAuth.getInstance().currentUser!!.email!!
                    //                  usersReference.child(userToReturn.displayName).setValue(userToReturn)
                    //groupsReference.child(userToReturn.sharedGroupName).child(userToReturn.displayName).setValue(userToReturn)
                    FirebaseDatabase.getInstance().getReference(currentUserUID).child("USER").setValue(User())
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun createAccount(email: String, password: String, displayName: String) {
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                        }
                    signIn(email, password)
                } else {
                    Toast.makeText(this, "Account creation failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun storeCredentials(view : View) {
        createAccount(email.text.toString(), password.text.toString(), username.text.toString())
        //FirebaseDatabase.getInstance().reference.child("MENSTRUATIE").setValue("I am gay")
    }
}
