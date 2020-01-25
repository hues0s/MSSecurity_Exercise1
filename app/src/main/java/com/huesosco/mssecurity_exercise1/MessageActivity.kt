package com.huesosco.mssecurity_exercise1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.huesosco.mssecurity_exercise1.utilities.AESClass
import java.lang.Exception

class MessageActivity : AppCompatActivity() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise1CollectionRef: CollectionReference = db.collection("exercise1")

    private lateinit var textView: TextView
    private lateinit var editText: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val passwordByUser: String = intent.extras?.getString("passUserTry")!!

        loadXmlReferences()
        loadMessage(passwordByUser)
        setUpButton(passwordByUser)
    }

    private fun loadXmlReferences(){
        textView = findViewById(R.id.activity_message_text_view)
        editText = findViewById(R.id.activity_message_edit_text)
        button = findViewById(R.id.activity_message_button)
    }

    private fun loadMessage(pass: String){
        exercise1CollectionRef
            .get()
            .addOnSuccessListener(object: OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {

                    if (querySnapshot != null) {

                        var messageEncrypted = String()

                        for(doc in querySnapshot.documents){
                            //we search for the message Document in the database, and the AES password, which
                            //is the one set before by the user
                            if(doc.id == "messageDoc") messageEncrypted = doc.data!!["message"].toString()
                        }

                        //once we have the message encrypted and its pass, we decrypt it
                        textView.text = if(messageEncrypted.isNotEmpty())
                            AESClass.decryptAES(messageEncrypted, pass)
                        else ""
                        Toast.makeText(applicationContext, "Message successfully loaded.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            .addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(applicationContext, "Something went wrong: ${p0.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setUpButton(pass: String) {
        button.setOnClickListener {
            val e1 = editText.text.toString()
            if (e1.isEmpty())
                Toast.makeText(applicationContext, "You must write a message.", Toast.LENGTH_SHORT).show()
            else{
                exercise1CollectionRef
                    .document("messageDoc").update("message", AESClass.encryptAES(e1, pass))
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Message successfully saved.", Toast.LENGTH_SHORT).show()
                        textView.text = e1
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "ERROR: Message could not be saved.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}
