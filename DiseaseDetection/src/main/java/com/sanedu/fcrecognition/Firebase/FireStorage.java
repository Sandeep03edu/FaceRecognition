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
import com.sanedu.common.Utils.ImageResizer;
import com.sanedu.fcrecognition.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Sandeep
 * Java class to access Firebase storage and its methods
 */
public class FireStorage {
    private static final String TAG = "FireStorageTag";
    private StorageReference rootStorage;

    /**
     * Constructor to initialise rootStorage
     * @param context - Context
     */
    public FireStorage(Context context) {
        this.rootStorage = FirebaseStorage.getInstance()
                .getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    /**
     * Method to upload Image to cloud
     * @param bitmap - Bitmap - ImageBitmap
     * @param folder - String - FolderName
     * @param fileName - String - FileName
     * @param listener - ImageUploadListener - listener to listen updates
     */
    public void uploadImage(Bitmap bitmap, String folder, String fileName, ImageUploadListener listener) {
        if (bitmap == null) {
            listener.onErrorUpload("Null Bitmap found");
            return;
        }

        // Converting bitmap to bytes array
        bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        StorageReference storageReference = rootStorage
                .child(folder)
                .child(fileName);

        // Uploading imageFile
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
                                        // Listener updating onGetting url
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listener.onErrorUpload(e.getMessage());
                                        // Listener updating onFailure
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onErrorUpload(e.getMessage());
                        // Listener updating onFailure
                    }
                });
    }

    /**
     * Interface used by FireStorage.java class
     */
    public interface ImageUploadListener {
        void getDownloadUrl(ArrayList<LabelledImage> imageUrl);
        void onErrorUpload(String err);
    }

}