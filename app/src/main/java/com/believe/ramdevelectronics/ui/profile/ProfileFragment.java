package com.believe.ramdevelectronics.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import com.believe.ramdevelectronics.Dialog.AddNumberDialog;
import com.believe.ramdevelectronics.MainActivity;
import com.believe.ramdevelectronics.R;
import com.believe.ramdevelectronics.UserInfoPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    private View root;
    private CircleImageView civUserProfileImage;
    private TextView tvEditProfile, tvUserName, tvUserEmail, tvAddNumber, tvUserPhoneNumber, tvAddAddress, tvUserAddress, tvManageAddress, tvLogout;

    public ProfileFragment() {
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.screenbackgroundcolor));
        setHasOptionsMenu(true);

        init();
        setUserData();

        tvAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNumberDialog addNumberDialog = new AddNumberDialog();
                addNumberDialog.show(getActivity().getSupportFragmentManager(),addNumberDialog.getTag());
            }
        });

        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddAddressActivity.class));
            }
        });

        tvManageAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ManageAddress.class));
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                // Pass data object in the bundle and populate details activity.
                Pair<View, String> p1 = Pair.create((View)civUserProfileImage, "profileImage");
                Pair<View, String> p2 = Pair.create((View)tvUserEmail, "userEmail");
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(intent, options.toBundle());
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
    }

    private void setUserData() {
        UserInfoPreferences userInfoPreferences = new UserInfoPreferences(Objects.requireNonNull(getActivity()));

        // USER NAME
        tvUserName.setText(userInfoPreferences.getUserName());

        // USER EMAIL
        tvUserEmail.setText(userInfoPreferences.getUserEmail());

        // USER PROFILE IMAGE
        Picasso.with(getActivity())
                .load(Uri.parse(userInfoPreferences.getUserProfileImageUrl()))
                .into(civUserProfileImage);

        // USER PHONE NUMBER
        if(!userInfoPreferences.getUserPhoneNumber().equals("")){
            tvAddNumber.setVisibility(View.GONE);
            tvUserPhoneNumber.setText(userInfoPreferences.getUserPhoneNumber());
        }else{
            tvAddNumber.setVisibility(View.VISIBLE);
            tvUserPhoneNumber.setVisibility(View.GONE);
        }

        // USER ADDRESS
        if(!userInfoPreferences.getPrimaryAddress().equals("")){
            tvAddAddress.setVisibility(View.GONE);
            tvUserAddress.setText(userInfoPreferences.getPrimaryAddress());
            tvManageAddress.setVisibility(View.VISIBLE);

            // USER ADDRESS TYPE
            if(userInfoPreferences.getPrimaryAddressType().equals("Home")){
                tvUserAddress.setCompoundDrawablesWithIntrinsicBounds(getActivity().getDrawable(R.drawable.home),null,null,null);
            }
            if(userInfoPreferences.getPrimaryAddressType().equals("Work")){
                tvUserAddress.setCompoundDrawablesWithIntrinsicBounds(getActivity().getDrawable(R.drawable.work),null,null,null);
            }

        }else{
            tvUserAddress.setVisibility(View.GONE);
            tvManageAddress.setVisibility(View.GONE);
        }
    }

    private void init() {
        civUserProfileImage = root.findViewById(R.id.civUserProfileImage);
        tvUserName = root.findViewById(R.id.tvUserName);
        tvUserEmail = root.findViewById(R.id.tvUserEmail);
        tvAddNumber = root.findViewById(R.id.tvAddNumber);
        tvUserPhoneNumber = root.findViewById(R.id.tvUserPhoneNumber);
        tvAddAddress = root.findViewById(R.id.tvAddAddress);
        tvUserAddress = root.findViewById(R.id.tvUserAddress);
        tvManageAddress = root.findViewById(R.id.tvManageAddress);
        tvLogout = root.findViewById(R.id.tvLogout);
        tvEditProfile = root.findViewById(R.id.tvEditProfile);
    }
}