package com.sanedu.fcrecognition.ScanHistory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.AnalysisResult.ResultPageActivity;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.ImageResizer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ScannedResultAdapter extends RecyclerView.Adapter<ScannedResultAdapter.ViewHolder> {

    private static final String TAG = "ScannedResultAdapterTag";
    private Context mContext;
    private ArrayList<FaceResult> faceResultArrayList;
    private ProgressDialog progressDialog;

    /**
     * Adapter constructor
     * @param mContext - Context - Activity context
     * @param faceResultArrayList - ArrayList<FaceResult> - faceResultArrayList
     */
    public ScannedResultAdapter(Context mContext, ArrayList<FaceResult> faceResultArrayList) {
        this.mContext = mContext;
        this.faceResultArrayList = faceResultArrayList;

        this.progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Fetching Result");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_scanned_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // FaceResult object
        FaceResult faceResult = faceResultArrayList.get(position);
        Log.d(TAG, "onBindViewHolder: Img: " + faceResult.getImageUrl());

        // Setting Result data
        holder.userName.setText(faceResult.getPatientName());
        holder.userAge.setText(faceResult.getAge() + " yr");
        holder.userGender.setText(faceResult.getGender());
        Picasso.get().load(faceResult.getImageUrl())
                .placeholder(R.drawable.ic_baseline_person_96)
                .into(holder.userImage);

        // Holder itemView onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                // Loading bitmap from Url and storing
                Picasso.get()
                        .load(faceResult.getImageUrl())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                dismissDialog();

                                // Converting Bitmap to byte array
                                bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] bytes = baos.toByteArray();

                                // Sending Bitmap to ResultPageActivity
                                Intent intent = new Intent(mContext, ResultPageActivity.class);
                                intent.putExtra(Constants.IMAGE_BITMAP_BYTES, bytes);
                                intent.putExtra(Constants.INTENT_RESULT, new Gson().toJson(faceResult));
                                mContext.startActivity(intent);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                dismissDialog();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                dismissDialog();
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return faceResultArrayList.size();
    }

    /**
     * Displaying dialog if not null
     */
    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    /**
     * Removing dialog if not null
     */
    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * ViewHolder to bind itemView views
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName, userAge, userGender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.cart_scanned_result_image);
            userName = itemView.findViewById(R.id.cart_scanned_result_name);
            userAge = itemView.findViewById(R.id.cart_scanned_result_age);
            userGender = itemView.findViewById(R.id.cart_scanned_result_gender);
        }
    }
}
