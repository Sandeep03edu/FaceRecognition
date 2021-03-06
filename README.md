# FaceRecognition
Simple android app created using OpenCv and dlib library

# Table of contents
* [Introduction](#introduction)
* [Technologies](#technologies)
* [Features](#features)
* [Dependencies](#dependencies)
* [Permissions](#permissions)
* [Working](#working)

# Introduction
- App has been made in 4 weeks during mentorship period for Microsoft Engage'22.
- The github repo basically contain two apps codebase.
- "DiseaseDetection" module works with "dlib", "sdk" and "common" module and used to detect facial parts and face diseases.
- "SmartAdvertising" module works with "sdk" and "common" module and used to display advertisment based on public age group and gender.

# Technologies
- Google Firebase 
  - Firebase mobile authentication to register verified users.
  - Firebase Firestore to store User details, User scan details, Advertisement documents.
  - Firebase storage to store images like user profile pic, scanned image.
  - Firebase Crashanalytics to detect crashes in app modules.
- OpenCv
  - OpenCv haarcascade_frontalface_alt2.xml file to detect human faces.
  - shape_predictor_68_face_landmarks.dat module to detect landmarks on faces.
  - age_deploy.prototxt and age_net.caffemodel to detect human age.
  - gender_deploy.prototxt and gender_net.caffemodel to detect human gender.

# Features
## Disease Detection
- Fetching multiple faces from a given image.
- Getting human face count from a given image.
- Adding 68 face landmark points to face.
- Splitted human face into small face parts like eyes, eyebrows, nose, lips and face.
- Fetching age and gender of given image with confidence value.
- Fetching human facial diseases symptoms percentage
  - Eyebrow Alopecia detection
  - Chapped Lips detection
  - Eye Redness detection
- Save result for future analysis and comparision.
- Rescan option to re-evaluate results with clear images.

## Smart Advertisement
- Displaying advertisement based on gender and age group of users.
- Priotizing advertisement based on parameter "lastDisplayedTime"
- Advertisement displayed in ascending order of "lastDisplayedTime" for a specific gender and age group
  - For e.g., With age group 8-15 and Gender Male 
  - Advertisement-1 with "lastDisplayedTime" - 1653763874560
  - Advertisement-2 with "lastDisplayedTime" - 1653763874234
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

# Working
## Disease detection

### Working flow

![DiseaseDetection Workflow](https://user-images.githubusercontent.com/73837113/170855211-7e096c32-e890-4ae4-a1ae-1b07df3bab86.jpg)

### Authentication
- User have to first authenticate in the app for using its functionality
- User have to login into his/her account
  <p><img src="https://user-images.githubusercontent.com/73837113/170852332-5b1e1120-994d-4041-b83e-2f6ebdd66336.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170852359-e4985e0c-44c6-4ebd-8a77-f662a887c32e.jpg" width="180" height="320"/></p>

- User should create his/her account
  <p><img src="https://user-images.githubusercontent.com/73837113/170852492-640e2c20-1970-4a6d-bb63-3d299b381aef.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170852494-07d991fc-64f2-49ac-a995-bc74a762d65e.jpg" width="180" height="320"/></p>

#### Testing account credentials (Not to be used for saving data)
  - Mobile number - 9999988888
  - OTP - 123456

### Choosing Image
- Home Screen
  <p><img src="https://user-images.githubusercontent.com/73837113/170853233-dbeb36ee-da84-4dc7-be70-9ca73f4f638a.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170853235-d6832891-da20-4e74-a5e4-e01b31e418a7.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170853236-21eadfc4-8b28-4f6f-9d3f-4d1987c1a550.jpg" width="180" height="320"/></p>

- Selected Image Cropping
  <p><img src="https://user-images.githubusercontent.com/73837113/170853333-6d6c42a6-3187-4ffe-9046-fef649a6a473.jpg" width="180" height="320"/></p>

### Results prediction
- Image Scanning
  <p><img src="https://user-images.githubusercontent.com/73837113/170853516-9b4a55d9-6312-47e0-9ad3-d8c487b53fd5.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170853517-5a6dc622-863a-4549-858f-31069bea60a8.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170853515-8ffde498-a2bc-4ed2-89e0-b656f1a3efe6.jpg" width="180" height="320"/></p>
  
- Predict age and gender with confidence percentage
  <p><img src="https://user-images.githubusercontent.com/73837113/170853690-ef7a9dee-7c31-419f-bd0d-c1b5221b34f0.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170853691-4e79c872-9628-4cab-b067-a9ec0df02c6d.jpg" width="180" height="320"/></p>

- Face parts analysis
  <p><img src="https://user-images.githubusercontent.com/73837113/170854373-13fcfea6-4c0e-4f8d-a862-b5bc6b42d2f0.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854372-4056eeef-82b8-4324-9ab3-758e99b35a6a.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854370-e55bb707-d064-4732-ae9a-f21ab99c944b.jpg" width="180" height="320"/></p>
  
- Face parts rescan facility 
  <p><img src="https://user-images.githubusercontent.com/73837113/170854463-ff968e72-c175-46ff-8e50-144119013214.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854462-d83213e5-3f6a-4be8-b6cd-6438642df1f4.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854461-f391d58c-ac6c-4129-a738-401ad13df1c7.jpg" width="180" height="320"/></p>
  <p><img src="https://user-images.githubusercontent.com/73837113/170854460-10a9de7b-f3e4-4fe2-8b70-b9a9ff5896c0.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854459-eadc0ecf-c75d-4344-8ceb-8908c5f5a36f.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854456-774f9a4f-5610-49b9-af35-a422c992a123.jpg" width="180" height="320"/></p>
  
- Saving user scan record in foreground service with notification
  <p><img src="https://user-images.githubusercontent.com/73837113/170854598-30e73452-f9d4-4e40-b013-969a50220246.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854595-5b297efb-c84f-414c-b0eb-290c93498e1d.jpg" width="180" height="320"/></p>
  <p></p>
  <p><img src="https://user-images.githubusercontent.com/73837113/170854601-ddae16eb-dec2-4281-882e-2e52c0cc2a5b.jpg" width="180" height="320"/>
  <img src="https://user-images.githubusercontent.com/73837113/170854599-4339c4b3-94a0-48f0-9dc2-fad51a62da9c.jpg" width="180" height="320"/></p>
 
 ### Additional features
 - Scan History, My profile and logout option
    <p><img src="https://user-images.githubusercontent.com/73837113/170854916-08eb0663-9696-4194-a191-047058a7be4b.jpg" width="180" height="320"/>
    <img src="https://user-images.githubusercontent.com/73837113/170854913-9ee092e1-9b9f-49b1-85e0-6e158c097f84.jpg" width="180" height="320"/>
    <img src="https://user-images.githubusercontent.com/73837113/170856552-7ccd1820-9628-4e82-af87-ce9d094a86dd.jpg" width="180" height="320"/></p>

## Smart Advertising
- Simple single screen app to display advetisements based on users age and gender.
- Advertisement changes at the interval of 30 seconds based on live audience in front of camera (Front camera).
- This application can be extended in public places like Malls, Shopping places, railway stations to display productive ads.

### Advertisement cloud documents
![Firebase Firestore Advertisement collection](https://user-images.githubusercontent.com/73837113/170864277-6b5d2cce-4866-409c-8eb8-ec2925e8c1be.PNG)


### Permission Requirements
- App needs to access Camera and External storage to function properly along with internet
    <p><img src="https://user-images.githubusercontent.com/73837113/170859038-55c9e77a-16bd-4323-be54-699f761d7a7f.jpg" width="180" height="320"/>
    <img src="https://user-images.githubusercontent.com/73837113/170859039-2d7cfbcf-1716-4049-8dc6-565559dfd68b.jpg" width="180" height="320"/></p>
    
### Output
- App detects image from Front camera and scan results from images
- Home screen, Male and Female advertisement is shown
    <p><img src="https://user-images.githubusercontent.com/73837113/170859097-363574ef-d1de-4c16-9e50-1de63a57099d.jpg" width="180" height="320"/>
    <img src="https://user-images.githubusercontent.com/73837113/170859098-7211a96a-0972-473f-a29e-892c55fe9403.jpg" width="180" height="320"/>
    <img src="https://user-images.githubusercontent.com/73837113/170859099-98862ca9-7605-448b-a887-82062154f63d.jpg" width="180" height="320"/></p> 
