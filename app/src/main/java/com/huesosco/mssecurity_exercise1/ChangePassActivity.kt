package com.huesosco.mssecurity_exercise1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.huesosco.mssecurity_exercise1.utilities.AESClass
import com.huesosco.mssecurity_exercise1.utilities.HashClass
import java.lang.Exception

class ChangePassActivity : AppCompatActivity() {


    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise1CollectionRef: CollectionReference = db.collection("exercise1")

    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var saveButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        loadXmlReferences()
        setUpButton()
    }


    private fun loadXmlReferences(){
        editText1 = findViewById(R.id.activity_change_pass_edit_pass1)
        editText2 = findViewById(R.id.activity_change_pass_edit_pass2)
        saveButton = findViewById(R.id.activity_change_pass_button)
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
                val newPass = HashClass.sha256(e1)
                updateMessageAESPass(newPass)
            }
        }
    }


    private fun updateMessageAESPass(newPass: String){

        var messageEncrypted = String()
        var oldPass = String()
        var messageDecrypted = String()

        exercise1CollectionRef
            .get()
            .addOnSuccessListener(object: OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {

                    if (querySnapshot != null) {

                        for(doc in querySnapshot.documents){
                            //we search for the message Document in the database, and the AES password, which
                            //is the one set before by the user
                            if(doc.id == "messageDoc")
                                messageEncrypted = doc.data!!["message"].toString()
                            if(doc.id == "passwordDoc")
                                oldPass = doc.data!!["pass"].toString()
                        }
                        if(messageEncrypted.isNotEmpty()){
                            //if there is a message stored, before changing the password,
                            // we have to change also the password used in the AES encryption
                            messageDecrypted = AESClass.decryptAES(messageEncrypted, oldPass)
                            exercise1CollectionRef
                                .document("messageDoc").update("message", AESClass.encryptAES(messageDecrypted, newPass))
                                .addOnSuccessListener {
                                    //finally, we update the pass after updating the message
                                    updatePass(newPass)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(applicationContext, "ERROR: Message pass could not be updated.", Toast.LENGTH_SHORT).show()
                                }
                        }
                        else //if we dont have any messages stored, we just update the password
                            updatePass(newPass)
                    }

                }
            })
            .addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(applicationContext, "Something went wrong: ${p0.message}", Toast.LENGTH_SHORT).show()
                }
            })

    }


    private fun updatePass(newPass: String){
        exercise1CollectionRef
            .document("passwordDoc").update("pass", newPass)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Password correctly saved.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "ERROR: Password could not be saved.", Toast.LENGTH_SHORT).show()
            }
    }

}
