# MSSecurity_Exercise1
Exercise done for the Mobile systems security subject, UAM Poznan.

"Write an app which will allow the user to pick a password which he will have to enter in order to access a short message saved on the phone (also entered by the user), or to change the password; of course entering the wrong password should result in lack of access to the message and the possibility of changing the password."

In order to increase the security, the password and the message are stored online using Cloud Firestore database from Google,
and the pass is encoded using SHA-256.
