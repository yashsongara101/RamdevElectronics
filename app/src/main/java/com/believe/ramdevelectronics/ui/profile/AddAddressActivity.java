package com.believe.ramdevelectronics.ui.profile;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.believe.ramdevelectronics.HomeActivity;
import com.believe.ramdevelectronics.R;
import com.believe.ramdevelectronics.UserInfoPreferences;
import com.believe.ramdevelectronics.VerifyMobile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddAddressActivity extends AppCompatActivity {

    private static final String TAG = "AddAddressActivity";

    private TextInputLayout etAddressLayout, etLandmarkLayout, etCityTownLayout, etPinCodeLayout;
    private TextInputEditText etAddress, etLandmark, etCityTown, etPinCode;
    private LinearLayout llHome, llWork;
    private TextView tvHome, tvWork;
    private Button btSaveAddress;
    private ImageView ivHome, ivWork;
    private RelativeLayout rlProgress;

    private Animation animShake;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.appbarcolor));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
        rgb2gray(ivHome, tvHome);
        rgb2gray(ivWork, tvWork);

        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gray2rgb(ivHome, tvHome);
                llHome.setTag("selected");
                llHome.setBackground(getDrawable(R.drawable.address_type_background_selected));

                rgb2gray(ivWork,tvWork);
                llWork.setTag("unselected");
                llWork.setBackground(getDrawable(R.drawable.address_type_background_unselected));
            }
        });

        llWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gray2rgb(ivWork,tvWork);
                llWork.setTag("selected");
                llWork.setBackground(getDrawable(R.drawable.address_type_background_selected));

                rgb2gray(ivHome, tvHome);
                llHome.setTag("unselected");
                llHome.setBackground(getDrawable(R.drawable.address_type_background_unselected));
            }
        });

        btSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String address = null, city = null, pincode = null, type = null;
                String landmark = "";

                if(etAddress.getText().toString().isEmpty()){
                    etAddressLayout.setErrorEnabled(true);
                    etAddressLayout.setError("Please enter address. You can't leave this field empty.");
                }else {
                    etAddressLayout.setErrorEnabled(false);
                    address = etAddress.getText().toString();
                }

                if(etCityTown.getText().toString().isEmpty()){
                    etCityTownLayout.setErrorEnabled(true);
                    etCityTownLayout.setError("Enter your city/town name.");
                }else {
                    etCityTownLayout.setErrorEnabled(false);
                    city = etCityTown.getText().toString();
                }

                if(etPinCode.getText().toString().isEmpty() ||
                        etPinCode.getText().toString().length() != etPinCodeLayout.getCounterMaxLength()){
                    etPinCodeLayout.setErrorEnabled(true);
                    etPinCodeLayout.setError("Enter valid pin code number.");
                }else{
                    etPinCodeLayout.setErrorEnabled(false);
                    pincode = etPinCode.getText().toString();
                }

                if(llHome.getTag() == llWork.getTag()){
                    llHome.startAnimation(AnimationUtils.loadAnimation(AddAddressActivity.this,R.anim.shake));
                    llWork.startAnimation(AnimationUtils.loadAnimation(AddAddressActivity.this,R.anim.shake));
                }

                if(!etAddress.getText().toString().isEmpty() &&
                    !etCityTown.getText().toString().isEmpty() &&
                    !etPinCode.getText().toString().isEmpty() &&
                        etPinCode.getText().toString().length() == etPinCodeLayout.getCounterMaxLength() &&
                        llHome.getTag() != llWork.getTag()){

                    String finalAddress = null;

                    if(llHome.getTag().equals("selected")){
                        type = "Home";
                    }

                    if(llWork.getTag().equals("selected")){
                        type = "Work";
                    }

                    if(!etLandmark.getText().toString().equals("")){
                        landmark = etLandmark.getText().toString();
                        finalAddress = address + ", " + landmark + ", " + city + ", Pin Code - " + pincode;
                    }else {
                        finalAddress = address + ", " + city + ", Pin Code - " + pincode;
                    }

                    uploadAddressToDatabase(finalAddress, type);

                }

            }
        });

    }

    private void uploadAddressToDatabase(final String address, final String addressType) {

        rlProgress.setVisibility(View.VISIBLE);

        Map<String, Object> addressObject = new HashMap<>();
        addressObject.put("Address", address);
        addressObject.put("AddressType", addressType);

        Task<DocumentReference> documentReference = mFirebaseFirestore.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("Addresses").add(addressObject);

        documentReference.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String primaryAddressKey = task.getResult().getId();
                setPrimaryAddress(address, primaryAddressKey, addressType);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddAddressActivity.this, "Try again.", Toast.LENGTH_SHORT).show();
                rlProgress.setVisibility(View.GONE);
            }
        });

    }

    private void setPrimaryAddress(final String address, final String primaryAddressKey, final String primaryAddressType) {

        Map<String, Object> addressObject = new HashMap<>();
        addressObject.put("primaryAddress", address);
        addressObject.put("primaryAddressKey", primaryAddressKey);
        addressObject.put("primaryAddressType",primaryAddressType);

        mFirebaseFirestore.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .update(addressObject)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        UserInfoPreferences preferences = new UserInfoPreferences(AddAddressActivity.this);
                        preferences.setPrimaryAddress(address);
                        preferences.setPrimaryAddressKey(primaryAddressKey);
                        preferences.setPrimaryAddressType(primaryAddressType);

                        rlProgress.setVisibility(View.GONE);

                        Intent intent = new Intent(AddAddressActivity.this,HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }

    private void gray2rgb(ImageView iv, TextView tv) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(1);
        iv.setColorFilter(new ColorMatrixColorFilter(matrix));

        tv.setTextColor(getColor(R.color.green));
    }

    private void rgb2gray(ImageView iv, TextView tv) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        iv.setColorFilter(new ColorMatrixColorFilter(matrix));

        tv.setTextColor(getColor(android.R.color.darker_gray));
    }

    private void init() {
        etAddressLayout = findViewById(R.id.etAddressLayout);
        etLandmarkLayout = findViewById(R.id.etLandmarkLayout);
        etCityTownLayout = findViewById(R.id.etCityTownLayout);
        etPinCodeLayout = findViewById(R.id.etPinCodeLayout);

        etAddress = findViewById(R.id.etAddress);
        etLandmark = findViewById(R.id.etLandmark);
        etCityTown = findViewById(R.id.etCityTown);
        etPinCode = findViewById(R.id.etPinCode);

        llHome = findViewById(R.id.llHome);
        llWork = findViewById(R.id.llWork);

        tvHome = findViewById(R.id.tvHome);
        tvWork = findViewById(R.id.tvWork);
        btSaveAddress = findViewById(R.id.btSaveAddress);

        ivHome = findViewById(R.id.ivHome);
        ivWork = findViewById(R.id.ivWork);

        rlProgress = findViewById(R.id.rlProgress);

        animShake = AnimationUtils.loadAnimation(this, R.anim.shake);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
