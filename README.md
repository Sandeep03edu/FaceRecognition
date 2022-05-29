# FaceRecognition
Simple android app created using OpenCv and dlib libraray

# Table of contents
* [Introduction](#introduction)
* [Technologies](#technologies)
* [Features](#features)
* [Dependencies](#dependencies)
* [Permissions](#permissions)

# Introduction
- App was made during 4 week mentorship program of Microsoft Engage'22.
- The github repo basically contains two apps codebase.
- "DiseaseDetection" module works with "dlib", "sdk" and "common" module and used to detect facial parts and face diseases.
- "SmartAdvertising" module works with "skd" and "common" module and used to display advertisment based on public age group and gender.

# Technologies
- Google Firebase 
  - Firebase mobile authentication to regustered verified users
  - Firebase Firestore to store User details, User scan details, Advertisement documets.
  - Firebase storage to store image file like user profile pic, scanned result image.
  - Firebase Crashanalytics to detect crashes in app modules
- OpenCv
  - OpenCv haarcascade_frontalface_alt2.xml file to detect human faces
  - shape_predictor_68_face_landmarks.dat module to detect landmarks on faces
  - age_deploy.prototxt and age_net.caffemodel to detect human age
  - gender_deploy.prototxt and gender_net.caffemodel to detect human gender

# Features
## Disease Detection
- Fetching multiple faces from a given image
- Returning human face count from a given image
- Adding 68 face landmark points to face
- Splitted human face image into many small face parts like eyes, eyebrows, nose, lips and face
- Fetching age and gender for a given image with confidence value
- Fetched many human facial diseases symptoms percentage
  - Eyebrow Alopecia detection
  - Chapped Lips detection
  - Eye Redness detection
- Saving result for future analysis and comparision
- Rescan option to reevaluate results with clear images 

## Smart Advertisement
- Displaying advertisement to users based on gender and age group
- Priotizing advertisement based on last displayed time
- Advertisement displayed in ascending order of last display time for a specific gender and age group
  - For e.g., With age group 8-15 and Gender Male 
  - Advertisement-1 with last display time - 1653763874560
  - Advertisement-2 with last display time - 1653763874234
  - Then Advertisement-2 will be displayed prior to Advertisement-1

# Dependencies
- OpenCv Sdk modules
- dlib facelandmark module
- Firebase
  - firebase-analytics
  - firebase-auth
  - firebase-firestore
  - firebase-storage
  - firebase-crashlytics
- Tensorflow dependencies
  - tensorflow-android
  - tensorflow-lite
- Gson dependency
  - com.squareup.retrofit2:converter-gson
- Image cropping API
  - com.theartofdev.edmodo:android-image-cropper
- Circle ImageView
  - de.hdodenhof:circleimageview
- Picasso to load images from url
  - com.squareup.picasso:picasso
- Updated workmanager for Foreground service
  - androidx.work:work-runtime

# Permissions
- Internet permission
  - To access advertisement in smartAdvertisement and authentication, saving records in diseaseDetection
- External Storage
  - To access media files inside the mobile phone
- Camera
  - To capture photo 
