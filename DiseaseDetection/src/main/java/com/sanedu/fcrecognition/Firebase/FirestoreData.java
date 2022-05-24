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

    public void updateUserImage(String uId, String imageUrl, FirestoreListener listener){
        userCollection.document(uId)
                .update("avatar", imageUrl)
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

    public void getUserByPhoneNumber(String phoneNum, UserListener listener){
        userCollection
                .whereEqualTo(Constants.PHONE_NUMBER, phoneNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()!=1){
                            listener.onFailure("Error fetching user details");
                        }
                        else{
                            DocumentSnapshot snapshot =queryDocumentSnapshots.getDocuments().get(0);
                            User user = snapshot.toObject(User.class);
                            if(user==null){
                                listener.onFailure("Error fetching user details");
                                return;
                            }
                            listener.onSuccess(user);
                        }
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

    public void getPastScannedHistory(ResultListener listener){
        recordCollection
                .whereEqualTo("uId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderBy("uploadTime")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots==null){
                            listener.onFailure(Constants.AN_ERROR);
                            return;
                        }

                        ArrayList<FaceResult> faceResultArrayList = new ArrayList<>();
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            FaceResult result = snapshot.toObject(FaceResult.class);
                            faceResultArrayList.add(result);
                        }
                        listener.onSuccess(faceResultArrayList);
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
        void onFailure(String err);
    }

    public interface UserListener{
        void onSuccess(User user);
        void onFailure(String err);
    }

    public interface ResultListener{
        void onSuccess(ArrayList<FaceResult> faceResultArrayList);
        void onFailure(String err);
    }
}
