package com.believe.ramdevelectronics.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.believe.ramdevelectronics.HomeActivity;
import com.believe.ramdevelectronics.MainActivity;
import com.believe.ramdevelectronics.Model.UserData;
import com.believe.ramdevelectronics.R;
import com.believe.ramdevelectronics.UserInfoPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    public static final String NIGHT_MODE = "NIGHT_MODE";

    private HomeViewModel homeViewModel;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;

    private View root;
    private ImageButton ibAppMode;

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        SharedPreferences appModePref = getActivity().getSharedPreferences("AppMode", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            SharedPreferences.Editor appModeEditor = appModePref.edit();
            appModeEditor.putBoolean("isNightMode",false);
            appModeEditor.commit();

            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        if(appModePref.getBoolean("isNightMode",false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.screenbackgroundcolor));
        setHasOptionsMenu(true);

        init();

        ibAppMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences appModePref = getActivity().getSharedPreferences("AppMode", Context.MODE_PRIVATE);
                SharedPreferences.Editor appModeEditor = appModePref.edit();
                appModeEditor.commit();
                if(!appModePref.getBoolean("isNightMode",false)){
                    appModeEditor.putBoolean("isNightMode",true);
                    appModeEditor.commit();
                    getActivity().recreate();
                }else {
                    appModeEditor.putBoolean("isNightMode",false);
                    appModeEditor.commit();
                    getActivity().recreate();
                }
            }
        });

        return root;
    }

    private void init() {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ibAppMode = root.findViewById(R.id.ibAppMode);
    }
}