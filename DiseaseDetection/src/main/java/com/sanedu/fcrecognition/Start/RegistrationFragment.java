package com.sanedu.fcrecognition.Start;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Firebase.FirebaseAuthentication;
import com.sanedu.fcrecognition.Firebase.FirestoreData;
import com.sanedu.fcrecognition.Home.HomeActivity;
import com.sanedu.fcrecognition.Model.User;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.LayoutUtils;
import com.sanedu.fcrecognition.Utils.SharedPrefData;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegistrationFragment - For registering non registered users
 */
public class RegistrationFragment extends Fragment {

    public RegistrationFragment() {
        // Required empty public constructor
    }

    // Fragment views
    EditText phoneNumberEt, otpEt, usernameEt;
    LinearLayout otpLl;
    TextView registrationBtn, loginAcc;

    ProgressDialog progressDialog;
    FirebaseAuthentication authentication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        // Initialising views
        _init(view);

        // Sending Otp
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LayoutUtils.checkFilled(new EditText[]{phoneNumberEt, usernameEt})) {
                    SendOtp();
                } else {
                    Toast.makeText(getContext(), Constants.ALL_COMP, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Move to login screen
        SwitchToLogin();

        return view;
    }

    /**
     * Changing viewpager page to LoginFragment
     */
    private void SwitchToLogin() {
        loginAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthenticationActivity activity = (AuthenticationActivity) getActivity();
                // Changing viewPager position
                if (activity != null)
                    activity.SetInitialPager(Constants.START_LOGIN);
            }
        });
    }

    /**
     * Sending otp for registration
     */
    private void SendOtp() {
        String phoneNum = phoneNumberEt.getText().toString().trim();
        String numberPatternRegex = "[0-9]+";

        // Checking phoneNumber
        Pattern numberPattern = Pattern.compile(numberPatternRegex);
        Matcher matcher = numberPattern.matcher(phoneNum);
        if (phoneNum.length() != 10 || !matcher.matches()) {
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        String prefPhoneNum = "+91" + phoneNum;
        showDialog();

        // Disabling views
        LayoutUtils.disableViews(new View[]{usernameEt, phoneNumberEt, registrationBtn});

        authentication = new FirebaseAuthentication();
        authentication.registerUser(prefPhoneNum, getActivity(), new FirebaseAuthentication.LoginListener() {
            @Override
            public void onFailure(String reason) {
                dismissDialog();
                // Enabling views onFailure
                LayoutUtils.enableViews(new View[]{usernameEt, phoneNumberEt, registrationBtn});
                Toast.makeText(getContext(), reason, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEnterOtp() {
                dismissDialog();
                // Enabling views onSuccess
                LayoutUtils.enableViews(new View[]{registrationBtn});

                otpLl.setVisibility(View.VISIBLE);
                registrationBtn.setText(getResources().getString(R.string.registration));

                // Verify Otp
                registrationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Checking otpFilled or not
                        if (LayoutUtils.checkFilled(new EditText[]{otpEt})) {
                            VerifyOtp();
                        } else {
                            Toast.makeText(getContext(), Constants.ALL_COMP, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    /**
     * Method to verify Otp entered by user
     */
    private void VerifyOtp() {
        String otp = otpEt.getText().toString().trim();

        // Checking otp length
        if (otp.length() != 6) {
            Toast.makeText(getContext(), "Please enter a valid Otp", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle("Verifying Otp");
        showDialog();
        LayoutUtils.disableViews(new View[]{usernameEt, phoneNumberEt, registrationBtn, otpEt});

        // Verifying Otp
        authentication.verifyOtp(otp, new FirebaseAuthentication.SignInListener() {
            @Override
            public void onSuccessLogin() {
                // Creating user Object
                User user = new User(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                        usernameEt.getText().toString().trim(),
                        phoneNumberEt.getText().toString().trim(),
                        Constants.AVATAR,
                        System.currentTimeMillis());

                // Adding user to collection
                FirestoreData firestoreData = new FirestoreData();
                firestoreData.addUser(user, new FirestoreData.FirestoreListener() {
                    @Override
                    public void onSuccess() {
                        dismissDialog();
                        LayoutUtils.enableViews(new View[]{usernameEt, phoneNumberEt, registrationBtn, otpEt});

                        // Save User Data and then move to Main Activity
                        SharedPrefData.addUser(requireContext(), user);

                        Intent mainActIntent = new Intent(getContext(), HomeActivity.class);
                        mainActIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActIntent);
                        requireActivity().finish();
                    }

                    @Override
                    public void onFailure(String e) {
                        dismissDialog();
                        LayoutUtils.enableViews(new View[]{usernameEt, phoneNumberEt, registrationBtn, otpEt});
                    }
                });

            }

            @Override
            public void onFailureLogin(Exception e) {
                LayoutUtils.enableViews(new View[]{usernameEt, phoneNumberEt, registrationBtn, otpEt});
                dismissDialog();
                Toast.makeText(getContext(), Constants.AN_ERROR + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
     * Initialising views
     * @param v - View inflated by fragment
     */
    private void _init(View v) {
        usernameEt = v.findViewById(R.id.registration_username_et);
        phoneNumberEt = v.findViewById(R.id.registration_phone_number_et);
        otpEt = v.findViewById(R.id.registration_otp);
        otpLl = v.findViewById(R.id.registration_otp_ll);
        registrationBtn = v.findViewById(R.id.registration_registration_btn);
        loginAcc = v.findViewById(R.id.registration_create_account);

        otpLl.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Sending Otp");
        progressDialog.setMessage("Please wait...");
    }
}