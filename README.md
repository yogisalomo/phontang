phontang
Created by: Yogi Salomo Mangontang Pratama
========

Java Application For Dropbox Photo Uploading. v0.1

Critical Assumption:
- The User using this App has a dropbox account
- You will always upload an image file (.jpg, .png)

How it works:

This app is used for uploading photos into Your Dropbox Account.
But everytime this app is called, it checks whether you have internet connection or not.
If you are connected, then it will upload your photos directly to your dropbox account.
If you are not connected,then it will save your photos in the temporary directory within this app, and later on you could
upload these photos when you have sufficient internet Connection.

How to Use:

Add the remote of this repository in your Eclipse Project directory (For Maximum Performance).

What is lacking?
- The app is always asking for Authorization even if you are not connected to internet.
(For checking purpose, you could hardcode the connStatus to Offline and comment the internetConnection checking).
- The app could not support real time feature. Which means it cannot update your dropbox content until the app is reloaded.
- The app is not verifying the format of the file uploaded yet, so it assume you are uploading an image file
- The app is still using a text file to list the temporary file saved and it caused problem when the program is about to be exported to executable jar.