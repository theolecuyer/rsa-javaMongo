# rsa-javaMongo
A Java RSA encryption program with MongoDB integration

Two of the java files are executable in their main methods:

App.java:
This file connects directly to the MongoDB database and requires
you to "sign on" with a username. If the username is not in the
system yet, it will assign a public and private key to that name.
Then it will allow messages to be sent to and from different users
and show the encryption process along the way.

GUI.java:
This file is meant to show a more graphical insight into the process.
Users will type in text in the bottom field and then hit enter. The
program will then show exactly what happens to encrypt their message
and then subsequently decrypt it.
