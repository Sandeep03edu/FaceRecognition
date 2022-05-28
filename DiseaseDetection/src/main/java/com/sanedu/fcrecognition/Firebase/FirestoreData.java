package com.sanedu.fcrecognition.Firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.Model.User;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Sandeep
 * Java class to implement Firebase Firestore and its methods
 */
public class FirestoreData {
    private CollectionReference userCollection;
    private CollectionReference recordCollection;
    private String myUserId;

    /**
     * Constructor
     * Initialising userCollection and recordCollection
     */
    public FirestoreData() {
        this.userCollection = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USER_TABLE);
        this.recordCollection = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_RECORD_TABLE);
        this.myUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    /**
     * Method to add user
     *
     * @param user     - User - new user
     * @param listener - FirestoreListener - listener to listen firebase changes
     */
    public void addUser(User user, FirestoreListener listener) {
        // Setting user document in collection
        userCollection.document(user.getuId())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                        // listener updating onSuccess
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                        // listener updating onFailure
                    }
                });
    }

    /**
     * Method to update userimage
     *
     * @param uId      - String - UserId
     * @param imageUrl - String - ImageUrl
     * @param listener - FirestoreListener - listener to listen firebase changes
     */
    public void updateUserImage(String uId, String imageUrl, FirestoreListener listener) {
        // Updating user image - "avatar" with userid- uId
        userCollection.document(uId)
                .update("avatar", imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                        // listener updating onSuccess
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                        // listener updating onFailure
                    }
                });

    }

    /**
     * Method to getUser details from PhoneNumber
     *
     * @param phoneNum - String - Phonenumber
     * @param listener - UserListener - listener to listen firebase changes
     */
    public void getUserByPhoneNumber(String phoneNum, UserListener listener) {
        userCollection
                .whereEqualTo(Constants.PHONE_NUMBER, phoneNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 1) {
                            // listener updating onFailure
                            listener.onFailure("Error fetching user details");
                        } else {
                            DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                            User user = snapshot.toObject(User.class);
                            if (user == null) {
                                // listener updating onFailure
                                listener.onFailure("Error fetching user details");
                                return;
                            }
                            // listener updating onSuccess
                            listener.onSuccess(user);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // listener updating onFailure
                        listener.onFailure(e.getMessage());
                    }
                });

    }

    /**
     * Method to upload Scan History data
     *
     * @param faceResult - FaceResult - Result data model Class
     * @param listener   - FirestoreListener - listener to listen firebase updates
     */
    public void uploadRecord(FaceResult faceResult, FirestoreListener listener) {
        // Setting document to collection
        recordCollection.document(faceResult.getResultId())
                .set(faceResult)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // listener updating onSuccess
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // listener updating onFailure
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Method to getPastScannedHistory
     *
     * @param listener - ResultListener - listener to listen firebase changes
     */
    public void getPastScannedHistory(ResultListener listener) {
        // Filtering results from collection
        recordCollection
                .whereEqualTo("uId", myUserId)
                .orderBy("uploadTime")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots == null) {
                            // listener updating onFailure
                            listener.onFailure(Constants.AN_ERROR);
                            return;
                        }

                        ArrayList<FaceResult> faceResultArrayList = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            FaceResult result = snapshot.toObject(FaceResult.class);
                            faceResultArrayList.add(result);
                        }
                        // listener updating onSuccess
                        listener.onSuccess(faceResultArrayList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // listener updating onFailure
                        listener.onFailure(e.getMessage());
                    }
                });

    }

    /**
     * Interface used by FirestoreData.java class
     */
    public interface FirestoreListener {
        void onSuccess();

        void onFailure(String err);
    }


    /**
     * Interface used by FirestoreData.java class
     */
    public interface UserListener {
        void onSuccess(User user);

        void onFailure(String err);
    }

    /**
     * Interface used by FirestoreData.java class
     */
    public interface ResultListener {
        void onSuccess(ArrayList<FaceResult> faceResultArrayList);

        void onFailure(String err);
    }
}
