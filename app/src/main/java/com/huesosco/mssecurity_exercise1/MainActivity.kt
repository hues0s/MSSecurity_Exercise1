package com.huesosco.mssecurity_exercise1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.biometric.BiometricManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.huesosco.mssecurity_exercise1.utilities.CustomBiometricPrompt
import com.huesosco.mssecurity_exercise1.utilities.PassCheckDialog
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise1CollectionRef: CollectionReference = db.collection("exercise1")

    private lateinit var buttonCreatePassword: Button
    private lateinit var buttonChangePassword: Button
    private lateinit var buttonAccessMessage: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var switchPasswordMode: Switch
    private lateinit var textViewSwitch: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadXmlReferences()

        setUpSwitch()
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
        switchPasswordMode = findViewById(R.id.main_switch_password_mode)
        textViewSwitch = findViewById(R.id.main_textview_switch)
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

                                    switchPasswordMode.visibility = View.INVISIBLE
                                    textViewSwitch.visibility = View.INVISIBLE
                                }
                                else {
                                    //we have pass, so we show the other buttons
                                    progressBar.visibility = View.GONE

                                    buttonCreatePassword.visibility = View.INVISIBLE
                                    buttonChangePassword.visibility = View.VISIBLE
                                    buttonAccessMessage.visibility = View.VISIBLE

                                    switchPasswordMode.visibility = View.VISIBLE
                                    textViewSwitch.visibility = View.VISIBLE
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
            val buttonType = "ACCESS_MESSAGE"
            if(switchPasswordMode.isChecked)
                //fingerprint dialog
                CustomBiometricPrompt(this, buttonType).getBiometricPromptDialog()
            else PassCheckDialog(buttonType).show(supportFragmentManager, "pass dialog")
        }
        buttonChangePassword.setOnClickListener {
            val buttonType = "CHANGE_PASSWORD"
            if(switchPasswordMode.isChecked)
                //fingerprint dialog
                CustomBiometricPrompt(this, buttonType).getBiometricPromptDialog()
            else PassCheckDialog(buttonType).show(supportFragmentManager, "pass dialog")
        }
    }

    private fun setUpSwitch(){

        textViewSwitch.text = "${resources.getString(R.string.current_pass_mode)} Text"

        switchPasswordMode.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                if(isChecked) {
                    if (BiometricManager.from(applicationContext).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                        //only allow to click the switch if the device has biometric sensor
                        textViewSwitch.text = "${resources.getString(R.string.current_pass_mode)} Fingerprint"
                    }
                    else {
                        Toast.makeText(applicationContext, "You cannot use fingerprint in this device.", Toast.LENGTH_SHORT).show()
                        switchPasswordMode.isChecked = false
                    }
                }
                else
                    textViewSwitch.text = "${resources.getString(R.string.current_pass_mode)} Text"
            }

        })
    }


}
