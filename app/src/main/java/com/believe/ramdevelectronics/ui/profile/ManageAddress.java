package com.believe.ramdevelectronics.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.believe.ramdevelectronics.HomeActivity;
import com.believe.ramdevelectronics.MainActivity;
import com.believe.ramdevelectronics.Model.AddressData;
import com.believe.ramdevelectronics.UserInfoPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.believe.ramdevelectronics.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ManageAddress extends AppCompatActivity {

    private static final String TAG = "ManageAddress";

    private RecyclerView rvAddressList;
    private List<AddressData> addressDataList = new ArrayList<>();
    private ManageAddressAdapter addressAdapter;
    private Button btAddNewAddress;
    private RelativeLayout rlProgress;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.appbarcolor));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

        getListData();

        btAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageAddress.this,AddAddressActivity.class));
            }
        });

    }

    private void getListData() {

        rlProgress.setVisibility(View.VISIBLE);

        final UserInfoPreferences infoPreferences = new UserInfoPreferences(ManageAddress.this);

        firebaseFirestore.collection("Users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("Addresses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                Map<String, Object> addressDataMap = documentSnapshot.getData();
                                Log.d(TAG, "onComplete: ------------------------------------------------------");
                                Log.d(TAG, "onComplete: " + addressDataMap.get("Address"));
                                Log.d(TAG, "onComplete: " + addressDataMap.get("AddressType"));
                                Log.d(TAG, "onComplete: " + documentSnapshot.getId());

                                AddressData addressData = new AddressData(addressDataMap.get("Address").toString(),
                                                                            addressDataMap.get("AddressType").toString(),
                                                                            documentSnapshot.getId());

                                if(infoPreferences.getPrimaryAddressKey().equals(documentSnapshot.getId())){
                                    addressDataList.add(0,addressData);
                                }else {
                                    addressDataList.add(addressData);
                                }
                            }
                            setUpAddressList(addressDataList);
                        }
                    }
                });

    }

    private void setUpAddressList(final List<AddressData> addressDataList) {

        rlProgress.setVisibility(View.VISIBLE);

        addressAdapter = new ManageAddressAdapter(ManageAddress.this, addressDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvAddressList.setLayoutManager(mLayoutManager);
        rvAddressList.setItemAnimator(new DefaultItemAnimator());
        rvAddressList.setAdapter(addressAdapter);

        rlProgress.setVisibility(View.GONE);

        addressAdapter.setOnItemClickListener(new ManageAddressAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(v.getTag().equals("N")){
                    AddressData data = addressDataList.get(position);
                    addressDataList.remove(position);
                    setPrimaryAddress(data, addressDataList);
                }

                if(v.getTag().equals("Y")){
                    Toast.makeText(ManageAddress.this, "This is already your primary address.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onItemLongClick(final int position, View v) {

                if(v.getTag().equals("N")){
                    new AlertDialog.Builder(ManageAddress.this)
                            .setTitle("Delete Address")
                            .setMessage("Are you sure you want to delete this address?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation

                                    rlProgress.setVisibility(View.VISIBLE);

                                    AddressData data = addressDataList.get(position);

                                    firebaseFirestore.collection("Users")
                                            .document(firebaseAuth.getCurrentUser().getUid())
                                            .collection("Addresses")
                                            .document(data.getUserAddressKey())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    addressDataList.remove(position);
                                                    setUpAddressList(addressDataList);
                                                    rlProgress.setVisibility(View.GONE);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ManageAddress.this, "Unable to delete.", Toast.LENGTH_SHORT).show();
                                            rlProgress.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(R.drawable.delete)
                            .show();
                }
            }
        });

    }

    private void setPrimaryAddress(final AddressData data,final List<AddressData> addressDataList) {

        rlProgress.setVisibility(View.VISIBLE);

        final String address = data.getUserAddress(),
                primaryAddressKey = data.getUserAddressKey(),
                primaryAddressType = data.getUserAddressType();

        Map<String, Object> addressObject = new HashMap<>();
        addressObject.put("primaryAddress", address);
        addressObject.put("primaryAddressKey", primaryAddressKey);
        addressObject.put("primaryAddressType",primaryAddressType);

        firebaseFirestore.collection("Users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .update(addressObject)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        UserInfoPreferences preferences = new UserInfoPreferences(ManageAddress.this);
                        preferences.setPrimaryAddress(address);
                        preferences.setPrimaryAddressKey(primaryAddressKey);
                        preferences.setPrimaryAddressType(primaryAddressType);

                        addressDataList.add(0,data);
                        setUpAddressList(addressDataList);
                        rlProgress.setVisibility(View.GONE);
                    }
                });
    }

    private void init() {
        rvAddressList = findViewById(R.id.rvAddressList);
        btAddNewAddress = findViewById(R.id.btAddNewAddress);
        rlProgress = findViewById(R.id.rlProgress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
