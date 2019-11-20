package com.huesosco.mssecurity_exercise1.utilities

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


class HashClass {

    companion object {

        fun sha256(s: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(s.toByteArray(StandardCharsets.UTF_8))
            val digest = md.digest()
            return String.format("%064x", BigInteger(1, digest))
        }

    }

}