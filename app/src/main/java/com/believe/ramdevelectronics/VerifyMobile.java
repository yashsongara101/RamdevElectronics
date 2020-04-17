package com.believe.ramdevelectronics;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.believe.ramdevelectronics.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyMobile extends AppCompatActivity {

    private static final String TAG = "VerifyMobile";

    private TextView tvOtpInformation;
    private Button btGetIn;
    private TextInputEditText etOTP;
    private TextInputLayout etOTPLayout;
    private RelativeLayout rlProgress;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;

    private String mobileNumber, verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.appbarcolor));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
        setMobileNumber();
        setOtpInformation();
        sendVerificationCode(mobileNumber);

        btGetIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = etOTP.getText().toString().trim();
                if(!code.isEmpty() && code.length()==6){
                    verifyCode(code);
                }else{
                    etOTPLayout.setError("Please enter code ...");
                    etOTPLayout.requestFocus();
                }
            }
        });
    }

    private void verifyCode(String code){
        rlProgress.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            setMobileNumberToDatabase();
                        }else{
                            Toast.makeText(VerifyMobile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            rlProgress.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifyMobile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                rlProgress.setVisibility(View.GONE);
            }
        });
    }

    private void setMobileNumberToDatabase() {
        Map<String, Object> data = new HashMap<>();
        data.put("userPhoneNumber", mobileNumber);

        mFirebaseFirestore.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        UserInfoPreferences infoPreferences = new UserInfoPreferences(VerifyMobile.this);
                        infoPreferences.setUserPhoneNumber(mobileNumber);

                        rlProgress.setVisibility(View.GONE);

                        Intent intent = new Intent(VerifyMobile.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                etOTP.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyMobile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void setOtpInformation() {
        tvOtpInformation.setText("We've sent an SMS with an activation code to your phone "+mobileNumber);
    }

    private void setMobileNumber() {
        mobileNumber = getIntent().getStringExtra("mobile");
    }

    private void init() {

        tvOtpInformation = findViewById(R.id.tvOtpInformation);
        btGetIn = findViewById(R.id.btGetIn);
        etOTP = findViewById(R.id.etOTP);
        etOTPLayout = findViewById(R.id.etOTPLayout);
        rlProgress = findViewById(R.id.rlProgress);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
