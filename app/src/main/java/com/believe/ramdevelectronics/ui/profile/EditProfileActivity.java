package com.believe.ramdevelectronics.ui.profile;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import com.believe.ramdevelectronics.Dialog.AddNumberDialog;
import com.believe.ramdevelectronics.Model.AddressData;
import com.believe.ramdevelectronics.UserInfoPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.believe.ramdevelectronics.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private CircleImageView civUserProfileImage;
    private TextView tvUserEmail, tvAddNumber, tvUserPhoneNumber, tvUnlinkMobileNumber;
    private TextInputEditText etUserName;
    private TextInputLayout etUserNameLayout;
    private Button btSaveProfile;
    private RelativeLayout rlProgress;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.appbarcolor));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
        setUpUserData();

        tvAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNumberDialog addNumberDialog = new AddNumberDialog();
                addNumberDialog.show(getSupportFragmentManager(),addNumberDialog.getTag());
            }
        });

        tvUnlinkMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditProfileActivity.this)
                        .setMessage("Are you sure you want to unlink your mobile number?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                rlProgress.setVisibility(View.VISIBLE);

                                Objects.requireNonNull(firebaseAuth.getCurrentUser())
                                        .unlink(PhoneAuthProvider.PROVIDER_ID)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Map<String, Object> phoneNumberObject = new HashMap<>();
                                                phoneNumberObject.put("userPhoneNumber", "");

                                                firebaseFirestore.collection("Users")
                                                        .document(firebaseAuth.getCurrentUser().getUid())
                                                        .update(phoneNumberObject)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                UserInfoPreferences preferences = new UserInfoPreferences(EditProfileActivity.this);
                                                                preferences.setUserPhoneNumber("");

                                                                setUpUserData();
                                                                rlProgress.setVisibility(View.GONE);
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, "Try after some time.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etUserName.getText().toString().isEmpty()){
                    etUserNameLayout.setErrorEnabled(true);
                    etUserNameLayout.setError("You can't leave this field empty.");
                }else{
                    etUserNameLayout.setErrorEnabled(false);

                    final String name = etUserName.getText().toString();

                    rlProgress.setVisibility(View.VISIBLE);

                    Map<String, Object> nameObject = new HashMap<>();
                    nameObject.put("userName", name);

                    firebaseFirestore.collection("Users")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .update(nameObject)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    UserInfoPreferences infoPreferences = new UserInfoPreferences(EditProfileActivity.this);
                                    infoPreferences.setUserName(name);

                                    supportFinishAfterTransition();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            rlProgress.setVisibility(View.GONE);
                            Toast.makeText(EditProfileActivity.this, "try after some time.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    private void setUpUserData() {
        UserInfoPreferences userInfoPreferences = new UserInfoPreferences(EditProfileActivity.this);

        // SETTING UP USER PROFILE IMAGE
        Picasso.with(EditProfileActivity.this)
                .load(Uri.parse(userInfoPreferences.getUserProfileImageUrl()))
                .fit()
                .into(civUserProfileImage);

        // SETTING UP USER EMAIL
        tvUserEmail.setText(userInfoPreferences.getUserEmail());

        // SETTING UP USER NAME
        etUserName.setText(userInfoPreferences.getUserName());

        // USER PHONE NUMBER
        if(!userInfoPreferences.getUserPhoneNumber().equals("")){
            tvAddNumber.setVisibility(View.GONE);
            tvUserPhoneNumber.setText(userInfoPreferences.getUserPhoneNumber());
            tvUnlinkMobileNumber.setVisibility(View.VISIBLE);
        }else{
            tvAddNumber.setVisibility(View.VISIBLE);
            tvUserPhoneNumber.setVisibility(View.GONE);
            tvUnlinkMobileNumber.setVisibility(View.GONE);
        }
    }

    private void init() {
        civUserProfileImage = findViewById(R.id.civUserProfileImage);

        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvAddNumber = findViewById(R.id.tvAddNumber);
        tvUnlinkMobileNumber = findViewById(R.id.tvUnlinkMobileNumber);
        tvUserPhoneNumber = findViewById(R.id.tvUserPhoneNumber);

        etUserName = findViewById(R.id.etUserName);
        etUserNameLayout = findViewById(R.id.etUserNameLayout);

        btSaveProfile = findViewById(R.id.btSaveProfile);

        rlProgress = findViewById(R.id.rlProgress);

        // FIREBASE
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

}
