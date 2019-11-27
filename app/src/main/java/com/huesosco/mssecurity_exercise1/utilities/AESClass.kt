package com.huesosco.mssecurity_exercise1.utilities

import android.util.Base64
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class AESClass {

    companion object{

        private val AES = "AES"

        fun decryptAES(outputString: String, password: String): String {
            val key = generateKey(password)
            val c = Cipher.getInstance(AES)
            c.init(Cipher.DECRYPT_MODE, key)
            val decodedValue = Base64.decode(outputString, Base64.DEFAULT)
            val decValue = c.doFinal(decodedValue)
            return String(decValue)
        }

        fun encryptAES(Data: String, password: String): String {
            val key = generateKey(password)
            val c = Cipher.getInstance(AES)
            c.init(Cipher.ENCRYPT_MODE, key)
            val encVal = c.doFinal(Data.toByteArray())
            return Base64.encodeToString(encVal, Base64.DEFAULT)
        }

        private fun generateKey(password: String): SecretKeySpec {
            //we use sha-512 because if the case any one enters in the DB, the password is stored in sha256,
            //and could be used to decrypt the AES message.
            val digest = MessageDigest.getInstance("SHA-512")
            val bytes = password.toByteArray(charset("UTF-8"))
            digest.update(bytes, 0, bytes.size)
            var key = digest.digest()
            key = Arrays.copyOfRange(key, 0, 32) //we get a key length of 256 bits
            return SecretKeySpec(key, "AES")
        }
    }

}