package com.sanedu.fcrecognition.Firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanedu.fcrecognition.Model.LabelledImage;
import com.sanedu.fcrecognition.Model.User;
import com.sanedu.fcrecognition.Utils.ImageResizer;
import com.sanedu.fcrecognition.Utils.SharedPrefData;
import com.sanedu.fcrecognition.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class FireStorage {
    private static final String TAG = "FireStorageTag";

    private StorageReference rootStorage;

    public FireStorage(Context context) {
        this.rootStorage = FirebaseStorage.getInstance()
                .getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    public void uploadImage(Bitmap bitmap, String folder, String fileName, ImageUploadListener listener) {
        if (bitmap == null) {
            listener.onErrorUpload("Null Bitmap found");
            return;
        }

        bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        StorageReference storageReference = rootStorage
                .child(folder)
                .child(fileName);

        UploadTask uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        ArrayList<String> uriArrayList = new ArrayList<>();
                                        LabelledImage labelledImage = new LabelledImage(uri.toString(), fileName);
                                        ArrayList<LabelledImage> labelledImageArrayList = new ArrayList<>();
                                        labelledImageArrayList.add(labelledImage);
                                        listener.getDownloadUrl(labelledImageArrayList);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listener.onErrorUpload(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onErrorUpload(e.getMessage());
                    }
                });
    }

    public void uploadMultiImages(Context context, String[] bitmapsUris, String[] folders, String[] fileNames, ImageUploadListener listener) {
        int n = bitmapsUris.length;
        if (n <= 0) {
            listener.onErrorUpload("An error occurred");
            return;
        }
        ArrayList<LabelledImage> labelledImageArrayList = new ArrayList<>();
        int i = 0;
        final int[] count = {0};
        for (String bitmapUri : bitmapsUris) {
            Bitmap bitmap = Utils.Uri2Bitmap(context, Uri.parse(bitmapUri));
            uploadImage(bitmap, folders[i], fileNames[i], new ImageUploadListener() {
                @Override
                public void getDownloadUrl(ArrayList<LabelledImage> labelledImages) {
                    count[0]++;
                    labelledImageArrayList.add(labelledImages.get(0));
                    if (count[0] == n) {
                        listener.getDownloadUrl(labelledImageArrayList);
                    }
                }

                @Override
                public void onErrorUpload(String err) {
                    count[0]++;
                    listener.onErrorUpload(err);
                    Log.e(TAG, "onErrorUpload: Err: " + err);
                }
            });
        }
    }

    public interface ImageUploadListener {
        void getDownloadUrl(ArrayList<LabelledImage> imageUrl);

        void onErrorUpload(String err);
    }

}