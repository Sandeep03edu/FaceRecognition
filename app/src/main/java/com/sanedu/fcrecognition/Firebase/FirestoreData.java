package com.sanedu.fcrecognition.Firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.Model.User;

public class FirestoreData {
    private FirebaseUser mUser;
    private CollectionReference userCollection;
    private CollectionReference recordCollection;

    public FirestoreData() {
        this.mUser = FirebaseAuth.getInstance().getCurrentUser();
        this.userCollection = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USER_TABLE);
        this.recordCollection = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_RECORD_TABLE);
    }

    public void addUser(User user, FirestoreListener listener){
        userCollection.document(user.getuId())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    public void uploadRecord(FaceResult faceResult, FirestoreListener listener){
        recordCollection.document(faceResult.getResultId())
                .set(faceResult)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    public interface FirestoreListener{
        void onSuccess();
        void onFailure(String e);
    }
}
