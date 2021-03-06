package com.sanedu.fcrecognition.Firebase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sanedu.common.Utils.Constants;

import java.util.concurrent.TimeUnit;

/**
 * @author Sandeep
 * Java class to implement authentication tasks
 */
public class FirebaseAuthentication {
    private static final String TAG = "FirebaseAuthTAG";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CollectionReference firestoreUserCollection;
    private String verificationId;
    protected SignInListener signInListener;

    /**
     * Constructor
     */
    public FirebaseAuthentication() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firestoreUserCollection = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USER_TABLE);
        this.mUser = mAuth.getCurrentUser();
    }

    /**
     * Function to registerUser
     *
     * @param phoneNumber   - String - Phone Number
     * @param activity      - Activity - Context
     * @param loginListener - LoginListener - listener for firebase
     */
    public void registerUser(String phoneNumber, Activity activity, LoginListener loginListener) {
        checkUserInDatabase(phoneNumber, new CheckUserListener() {
            @Override
            public void onUserExist(boolean exist) {
                if (exist) {
                    Log.d(TAG, "onUser Exist: ");
                    // Listener updating onFailure
                    loginListener.onFailure(Constants.USER_ALR_EXIST);
                } else {
                    verifyPhoneNum(activity, phoneNumber, new VerificationListener() {
                        @Override
                        public void onCodeSent() {
                            Log.d(TAG, "onCodeSent: ");
                            // Listener to enter Otp
                            loginListener.onEnterOtp();
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException err) {
                            Log.e(TAG, "onVerificationFailed: Err: " + err.getMessage());
                            // Listener updating onFailure
                            loginListener.onFailure(err.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCheckFailed(Exception e) {
                Log.e(TAG, "onCheckFailed: Err: " + e.getMessage());
                // Listener updating onFailure
                loginListener.onFailure(e.getMessage());
            }
        });
    }

    /**
     * Function to login existing users
     *
     * @param phoneNumber   - String - PhoneNumber
     * @param activity      - Activity - Context
     * @param loginListener - LoginListener - listener to listen firebase updates
     */
    public void loginUser(String phoneNumber, Activity activity, LoginListener loginListener) {
        String unPrefNum = phoneNumber;
        if (phoneNumber.startsWith("+91")) {
            unPrefNum = phoneNumber.replace("+91", "");
        }

        // Checking user in Firebase Firestore
        checkUserInDatabase(unPrefNum, new CheckUserListener() {
            @Override
            public void onUserExist(boolean exist) {
                if (!exist) {
                    Log.d(TAG, "onUser Not Exist: ");
                    // Listener updating onFailure
                    loginListener.onFailure(Constants.USER_DNE);
                } else {
                    verifyPhoneNum(activity, phoneNumber, new VerificationListener() {
                        @Override
                        public void onCodeSent() {
                            Log.d(TAG, "onCodeSent: ");
                            // Listener updating onCodeSent
                            loginListener.onEnterOtp();
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException err) {
                            Log.e(TAG, "onVerificationFailed: Err: " + err.getMessage());
                            // Listener updating onFailure
                            loginListener.onFailure(err.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCheckFailed(Exception e) {
                Log.e(TAG, "onCheckFailed: Err: " + e.getMessage());
                // Listener updating onFailure
                loginListener.onFailure(e.getMessage());
            }
        });
    }

    /**
     * Function to check user exists or not in Firestore
     *
     * @param phoneNumber       - String - PhoneNumber
     * @param checkUserListener - CheckUserListener - listener to listen firebase changes
     */
    public void checkUserInDatabase(String phoneNumber, CheckUserListener checkUserListener) {
        firestoreUserCollection.whereEqualTo(Constants.PHONE_NUMBER, phoneNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            // User exist
                            checkUserListener.onUserExist(true);
                        } else {
                            // User Doesn't exist
                            checkUserListener.onUserExist(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "checkUserInDatabase onFailure: ", e);
                        // Listener updating onFailure
                        checkUserListener.onCheckFailed(e);
                    }
                });
    }

    /**
     * Function to verify phoneNumber
     *
     * @param activity             - Activity - Context
     * @param phoneNumber          - String - PhoneNumber
     * @param verificationListener - VerificationListener - listener to listen firebase changes
     */
    private void verifyPhoneNum(Activity activity, String phoneNumber, VerificationListener verificationListener) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)       // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d(TAG, "onVerificationCompleted: ");
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e(TAG, "onVerificationFailed: ", e);
                                // Listener updating onFailure
                                verificationListener.onVerificationFailed(e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Log.d(TAG, "onCodeSent: String: " + s);
                                verificationId = s;
                                // Listener updating onCodeSent
                                verificationListener.onCodeSent();
                            }
                        }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * Method to verify Otp
     *
     * @param otp            - String - 6 digit otp value
     * @param signInListener - SignInListener - listener to listen firebase changes
     */
    public void verifyOtp(String otp, SignInListener signInListener) {
        this.signInListener = signInListener;
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredential(credential);
    }

    /**
     * Method to signIn User with phoneCredentials
     *
     * @param credential - PhoneAuthCredential
     */
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "signInWithCredential onSuccess: ");
                        // Listener updating onSuccess
                        signInListener.onSuccessLogin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "signInWithCredential onFailure: ", e);
                        // Listener updating onFailure
                        signInListener.onFailureLogin(e);
                    }
                });
    }

    /**
     * Interface for FirebaseAuthentication.java class
     */
    public interface VerificationListener {
        void onCodeSent();

        void onVerificationFailed(FirebaseException err);
    }

    /**
     * Interface for FirebaseAuthentication.java class
     */
    public interface SignInListener {
        void onSuccessLogin();

        void onFailureLogin(Exception e);
    }

    /**
     * Interface for FirebaseAuthentication.java class
     */
    public interface CheckUserListener {
        void onUserExist(boolean exist);

        void onCheckFailed(Exception e);
    }

    /**
     * Interface for FirebaseAuthentication.java class
     */
    public interface LoginListener {
        void onFailure(String reason);

        void onEnterOtp();
    }

}