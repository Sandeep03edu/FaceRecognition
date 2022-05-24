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

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    EditText phoneNumberEt, otpEt;
    LinearLayout otpLl;
    TextView loginBtn, createAcc;
    ProgressDialog progressDialog;
    FirebaseAuthentication authentication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        _init(view);

        // Sending Otp
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LayoutUtils.checkFilled(new EditText[]{phoneNumberEt})) {
                    SendOtpLogin();
                } else {
                    Toast.makeText(getContext(), Constants.ALL_COMP, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Move to registration screen
        SwitchToRegistration();

        return view;
    }

    private void SwitchToRegistration() {
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthenticationActivity activity = (AuthenticationActivity) getActivity();
                if (activity != null)
                    activity.SetInitialPager(Constants.START_REGISTRATION);
            }
        });
    }

    private void SendOtpLogin() {
        String phoneNum = phoneNumberEt.getText().toString().trim();
        String numberPatternRegex = "[0-9]+";
        Pattern numberPattern = Pattern.compile(numberPatternRegex);
        Matcher matcher = numberPattern.matcher(phoneNum);

        if (phoneNum.length() != 10 || !matcher.matches()) {
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        String prefPhoneNum = "+91" + phoneNum;

        showDialog();
        LayoutUtils.disableViews(new View[]{phoneNumberEt, loginBtn});
        authentication = new FirebaseAuthentication();
        authentication.loginUser(prefPhoneNum, getActivity(), new FirebaseAuthentication.LoginListener() {
            @Override
            public void onFailure(String reason) {
                dismissDialog();
                LayoutUtils.enableViews(new View[]{loginBtn, phoneNumberEt});
                Toast.makeText(getContext(), reason, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEnterOtp() {
                dismissDialog();
                LayoutUtils.enableViews(new View[]{loginBtn});

                otpLl.setVisibility(View.VISIBLE);
                loginBtn.setText(getResources().getString(R.string.login));

                // Verify Otp
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

    private void VerifyOtp() {
        String otp = otpEt.getText().toString().trim();
        if (otp.length() != 6) {
            Toast.makeText(getContext(), "Please enter a valid Otp", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle("Verifying Otp");
        showDialog();
        authentication.verifyOtp(otp, new FirebaseAuthentication.SignInListener() {
            @Override
            public void onSuccessLogin() {
                FirestoreData data = new FirestoreData();
                data.getUserByPhoneNumber(phoneNumberEt.getText().toString().trim(), new FirestoreData.UserListener() {
                    @Override
                    public void onSuccess(User user) {
                        dismissDialog();
                        SharedPrefData.addUser(requireContext(), user);
                        Intent mainActIntent = new Intent(getContext(), HomeActivity.class);
                        mainActIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActIntent);
                        requireActivity().finish();
                    }

                    @Override
                    public void onFailure(String e) {
                        dismissDialog();
                    }
                });
            }

            @Override
            public void onFailureLogin(Exception e) {
                dismissDialog();
                Toast.makeText(getContext(), Constants.AN_ERROR + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void _init(View v) {
        phoneNumberEt = v.findViewById(R.id.login_phone_number_et);
        otpEt = v.findViewById(R.id.login_otp);
        otpLl = v.findViewById(R.id.login_otp_ll);
        loginBtn = v.findViewById(R.id.login_login_btn);
        createAcc = v.findViewById(R.id.login_create_account);

        otpLl.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Sending Otp");
        progressDialog.setMessage("Please wait...");
    }
}