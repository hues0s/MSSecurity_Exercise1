package com.huesosco.mssecurity_exercise1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise1CollectionRef: CollectionReference = db.collection("exercise1")

    private lateinit var buttonCreatePassword: Button
    private lateinit var buttonChangePassword: Button
    private lateinit var buttonAccessMessage: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadXmlReferences()
        setUpButtons()

    }

    override fun onResume() {
        super.onResume()
        //we check it here in order to check it also after creating the password and going back to this activity
        checkIfPasswordHasBeenCreated()
    }

    private fun loadXmlReferences(){
        buttonCreatePassword = findViewById(R.id.main_button_create_password)
        buttonChangePassword = findViewById(R.id.main_button_change_password)
        buttonAccessMessage = findViewById(R.id.main_button_access_message)
        progressBar = findViewById(R.id.main_progress_bar)
    }

    private fun checkIfPasswordHasBeenCreated() {

        exercise1CollectionRef
            .get()
            .addOnSuccessListener(object: OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {

                    if (querySnapshot != null) {
                        for(doc in querySnapshot.documents){
                            //we search for the password Document in the database
                            if(doc.id == "passwordDoc"){
                                //once we have it, we check if a password has been previously created
                                if (doc.data!!["pass"] == "") {
                                    //no pass previously created. we show create password button
                                    progressBar.visibility = View.GONE
                                    buttonCreatePassword.visibility = View.VISIBLE
                                    buttonChangePassword.visibility = View.INVISIBLE
                                    buttonAccessMessage.visibility = View.INVISIBLE
                                }
                                else {
                                    //we have pass, so we show the other buttons
                                    progressBar.visibility = View.GONE
                                    buttonCreatePassword.visibility = View.INVISIBLE
                                    buttonChangePassword.visibility = View.VISIBLE
                                    buttonAccessMessage.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            })
            .addOnFailureListener(object: OnFailureListener{
                override fun onFailure(p0: Exception) {
                    Toast.makeText(applicationContext, "Something went wrong: ${p0.message}", Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun setUpButtons() {
        buttonCreatePassword.setOnClickListener {
            val i = Intent(applicationContext, ChangePassActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(i)
        }
        buttonAccessMessage.setOnClickListener {
            val dialog = PassCheckDialog("ACCESS_MESSAGE")
            dialog.show(supportFragmentManager, "pass dialog")
        }
        buttonChangePassword.setOnClickListener {
            val dialog = PassCheckDialog("CHANGE_PASSWORD")
            dialog.show(supportFragmentManager, "pass dialog")
        }
    }




}
