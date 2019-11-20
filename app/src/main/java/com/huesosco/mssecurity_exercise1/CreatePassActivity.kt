package com.huesosco.mssecurity_exercise1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.huesosco.mssecurity_exercise1.utilities.HashClass

class CreatePassActivity : AppCompatActivity() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise1CollectionRef: CollectionReference = db.collection("exercise1")

    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pass)


        loadXmlReferences()
        setUpButton()
    }


    private fun loadXmlReferences(){
        editText1 = findViewById(R.id.activity_create_pass_edit_pass1)
        editText2 = findViewById(R.id.activity_create_pass_edit_pass2)
        saveButton = findViewById(R.id.activity_create_pass_button)
    }

    private fun setUpButton(){

        saveButton.setOnClickListener {

            val e1 = editText1.text.toString()
            val e2 = editText2.text.toString()

            if (e1.isEmpty() || e2.isEmpty())
                Toast.makeText(applicationContext, "You must fill both gaps.", Toast.LENGTH_SHORT).show()
            else if(e1 != e2) //we check if the password in both edit text are equal
                Toast.makeText(applicationContext, "Passwords written are not equal.", Toast.LENGTH_SHORT).show()
            else{
                exercise1CollectionRef
                    .document("passwordDoc").update("pass", HashClass.sha256(e1))
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Password correctly saved.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "ERROR: Password could not be saved.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

}
