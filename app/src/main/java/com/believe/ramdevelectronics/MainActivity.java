package com.believe.ramdevelectronics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.believe.ramdevelectronics.Model.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1;

    private LinearLayout llGoogleSignInButton;
    private TextView tvWelcomeInfo;
    private ImageView ivIllustration;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()!=null){

            llGoogleSignInButton.setVisibility(View.GONE);
            ivIllustration.setVisibility(View.GONE);
            tvWelcomeInfo.setVisibility(View.GONE);

            userDataSetup();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("IsNewUser",false);
                    startActivity(intent);
                }
            }, 1000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getColor(R.color.screenbackgroundcolor));

        init();

    }

    private void userDataSetup() {
        mFirebaseFirestore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                if(documentSnapshot.getId().equals(mAuth.getCurrentUser().getUid())){

                                    Map<String, Object> userData = documentSnapshot.getData();

                                    Log.d(TAG, "onComplete: " + userData.get("userEmail"));
                                    Log.d(TAG, "onComplete: " + userData.get("userName"));
                                    Log.d(TAG, "onComplete: " + userData.get("userId"));
                                    Log.d(TAG, "onComplete: " + userData.get("userPhoneNumber"));
                                    Log.d(TAG, "onComplete: " + userData.get("userProfileImageUrl"));
                                    Log.d(TAG, "onComplete: " + userData.get("primaryAddress"));
                                    Log.d(TAG, "onComplete: " + userData.get("primaryAddressKey"));
                                    Log.d(TAG, "onComplete: " + userData.get("primaryAddressType"));

                                    UserInfoPreferences userInfoPreferences = new UserInfoPreferences(
                                            MainActivity.this,
                                            Objects.requireNonNull(userData.get("userName")).toString(),
                                            Objects.requireNonNull(userData.get("userEmail")).toString(),
                                            Objects.requireNonNull(userData.get("userId")).toString(),
                                            Objects.requireNonNull(userData.get("userProfileImageUrl")).toString(),
                                            Objects.requireNonNull(userData.get("userPhoneNumber")).toString(),
                                            Objects.requireNonNull(userData.get("primaryAddress")).toString(),
                                            Objects.requireNonNull(userData.get("primaryAddressKey")).toString(),
                                            Objects.requireNonNull(userData.get("primaryAddressType")).toString()
                                    );

                                }
                            }
                        }
                    }
                });
    }

    private void init() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        llGoogleSignInButton = findViewById(R.id.llGoogleSignInButton);
        tvWelcomeInfo = findViewById(R.id.tvWelcomeInfo);
        ivIllustration = findViewById(R.id.ivIllustration);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            isNewUser(task.getResult().getAdditionalUserInfo().isNewUser());
                            userDataSetup();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void isNewUser(boolean isNew) {
        Toast.makeText(getApplicationContext(), String.valueOf(isNew), Toast.LENGTH_SHORT).show();
        FirebaseUser user = mAuth.getCurrentUser();

        if(isNew){
            UserData userData = new UserData(
                    user.getDisplayName(),
                    user.getEmail(),
                    user.getUid(),
                    user.getPhotoUrl().toString(),
                    "",
                    "",
                    "",
                    ""
            );

            UserInfoPreferences userInfoPreferences = new UserInfoPreferences(
                    MainActivity.this,
                    user.getDisplayName(),
                    user.getEmail(),
                    user.getUid(),
                    user.getPhotoUrl().toString(),
                    "",
                    "",
                    "",
                    ""
            );

            mFirebaseFirestore.collection("Users").document(user.getUid()).set(userData);
        }
    }

}
