# Mobile Systems Security : Exercise1
Exercise done for the Mobile systems security subject, UAM Poznan.

"Write an app which will allow the user to pick a password which he will have to enter in order to access a short message saved on the phone (also entered by the user), or to change the password; of course entering the wrong password should result in lack of access to the message and the possibility of changing the password."

In order to increase the security, the password and the message are stored online using Cloud Firestore database from Google. Everything gets reset at the beginning of the app, and a new salt is generated in every execution. The pass gets the salt added, and then is encoded using SHA-256, and the message is encoded using AES-128. In order to check whether the user has written the correct password, the app checks if the text written by the user plus the salt (all encoded using SHA-256) equals the pass stored in the online DB. Also, it is important to mention that the password used to encode and decode the message is the plain text introduced by the user, so that if someone reads the data stored in the DB, he won't be able to decode the message.
