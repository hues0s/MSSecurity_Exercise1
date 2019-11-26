package com.huesosco.mssecurity_exercise1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.huesosco.mssecurity_exercise1.utilities.HashClass
import java.lang.Exception


class PassCheckDialog(private val buttonType: String): DialogFragment() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var exercise1CollectionRef: CollectionReference = db.collection("exercise1")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val dialogView = inflater.inflate(R.layout.dialog_pass_check, container, false)
        val dialogEditText = dialogView.findViewById(R.id.dialog_pass_check_edit_text) as EditText
        val dialogButton = dialogView.findViewById(R.id.dialog_pass_check_button) as Button


        dialogButton.setOnClickListener {
            checkPassCorrectness(dialogView, dialogEditText.text.toString())
        }

        return dialogView
    }


    private fun checkPassCorrectness(dialogView: View, passTry: String){
        exercise1CollectionRef
            .get()
            .addOnSuccessListener(object: OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {

                    if (querySnapshot != null) {
                        for(doc in querySnapshot.documents){
                            //we search for the password Document in the database
                            //the pass try in sha256 must be equal to the one saved and encoded before
                            if(doc.id == "passwordDoc" && doc.data!!["pass"] == HashClass.sha256(passTry)){
                                if(buttonType == "ACCESS_MESSAGE"){
                                    val i = Intent(dialogView.context, MessageActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    dialogView.context.startActivity(i)
                                }
                                else if(buttonType == "CHANGE_PASSWORD"){
                                    val i = Intent(dialogView.context, ChangePassActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    dialogView.context.startActivity(i)
                                }
                                dismiss()
                            }
                            else{
                                Toast.makeText(dialogView.context, "The password written is wrong.", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        }
                    }
                }
            })
            .addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    Toast.makeText(dialogView.context, "Something went wrong: ${p0.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


}