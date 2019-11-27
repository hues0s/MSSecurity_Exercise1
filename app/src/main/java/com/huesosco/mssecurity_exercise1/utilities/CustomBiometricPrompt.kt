package com.huesosco.mssecurity_exercise1.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.huesosco.mssecurity_exercise1.ChangePassActivity
import com.huesosco.mssecurity_exercise1.MessageActivity
import java.util.concurrent.Executors


class CustomBiometricPrompt(private val fragmentActivity: FragmentActivity, private val buttonType: String) {

    private var biometricPrompt: BiometricPrompt

    init{
        val executor = Executors.newSingleThreadExecutor()
        biometricPrompt = BiometricPrompt(fragmentActivity, executor, getCustomBiometricAuthenticationCallback())
    }

    private fun getCustomBiometricAuthenticationCallback(): BiometricPrompt.AuthenticationCallback{

        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                //called when a fingerprint is matched successfully
                super.onAuthenticationSucceeded(result)
                fragmentActivity.runOnUiThread {
                    if(buttonType == "ACCESS_MESSAGE"){
                        val i = Intent(fragmentActivity, MessageActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        fragmentActivity.startActivity(i)
                    }
                    else if(buttonType == "CHANGE_PASSWORD"){
                        val i = Intent(fragmentActivity, ChangePassActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        fragmentActivity.startActivity(i)
                    }
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                //called when the authentication process cannot be completed successfully
                super.onAuthenticationError(errorCode, errString)
                fragmentActivity.runOnUiThread {
                    Toast.makeText(fragmentActivity, "ERROR: an unrecoverable error has been encountered and the operation is complete.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPromptInfo() : BiometricPrompt.PromptInfo {

        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Enter your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

    }

    fun getBiometricPromptDialog(){
        return biometricPrompt.authenticate(getPromptInfo())
    }

}