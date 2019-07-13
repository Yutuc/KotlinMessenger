package com.example.kotlinmessenger.registerlogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "Login"

        //calls performLogin function
        login_button_login.setOnClickListener {
            performLogin()
        }

        //go to registration page
        create_account_textview.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            //clears email and password fields
            email_edittext_login.text.clear()
            password_edittext_login.text.clear()
        }

    }//onCreate function

    //logins the user to firebase
    private fun performLogin(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        //if the fields are empty
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email/password", Toast.LENGTH_SHORT).show()
            return
        }

        //authenticate and log in user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) { return@addOnCompleteListener }
                //if login successful
                Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //clears the stack of activities
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "Failed to login user: ${it?.message}")
                Toast.makeText(this, "Failed to login user", Toast.LENGTH_SHORT).show()

            }
    }//perform Login function
}
