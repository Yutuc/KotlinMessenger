package com.example.kotlinmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    //this is where Kotlin defines global variables
    companion object {
        var selectedPhotoUri : Uri? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title = "Register"

        //calls performRegistration method
        register_button_register.setOnClickListener {
            performRegistration()
        }

        //goes back to login page
        already_have_account_textView.setOnClickListener {
            finish()
        }

        //select a photo button action
        select_photo_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }//onCreate function

    //runs after an activity is finished (specifically when the photo selector is closed)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null){
            //shows photo selected in a circular image view
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            select_photo_imageview.setImageBitmap(bitmap)
            select_photo_button_register.alpha = 0f //set the buttons transparency to translucent
        }
    }//OnActivityResult function

    private fun performRegistration(){
        val username = username_edit_text.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        //if username or email or password text fields are empty
        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username/email/password", Toast.LENGTH_SHORT).show()
            return
        }
        //if a photo isn't selected
        else if(selectedPhotoUri == null){
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show()
            return
        }

        //creating a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){ return@addOnCompleteListener }
                //creates an authenticated account for the user
                Log.d("RegisterActivity", "Successfully authenticated and created user with uid: ${it.result?.user?.uid}")
                uploadImageToFirebaseStorage() //uploads image to firebase storage
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
            }
    }//performRegistration function

    //saves users profile picture to firebase storage
    private fun uploadImageToFirebaseStorage(){

        val fileName  = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        //pushes the file to firebase storage
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }//uploadImageToFirebaseStorage function

    //saves a User object to the firebase database
    private fun saveUserToFirebaseDatabase(profileImageUrl : String){
        val uid = FirebaseAuth.getInstance().uid ?: "" // <--- Elvis Operator
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edit_text.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Successful registration", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //clears the stack of activities
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to save user to firebase database ${it.message}")
            }
    }//saveUserToFirebaseDatabase function

}//RegisterActivity class
