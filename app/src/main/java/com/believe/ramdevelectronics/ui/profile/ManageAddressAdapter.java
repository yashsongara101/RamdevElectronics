package com.believe.ramdevelectronics.ui.profile;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.believe.ramdevelectronics.Model.AddressData;
import com.believe.ramdevelectronics.R;
import com.believe.ramdevelectronics.UserInfoPreferences;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class ManageAddressAdapter extends RecyclerView.Adapter<ManageAddressAdapter.MyViewHolder>{

    private Activity activity;
    private List<AddressData> addressDataList;
    private RadioButton rbAddress;
    private UserInfoPreferences infoPreferences;
    private static ClickListener clickListener;

    public ManageAddressAdapter(Activity activity, List<AddressData> addressDataList) {
        this.activity = activity;
        this.addressDataList = addressDataList;
        infoPreferences = new UserInfoPreferences(activity);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            rbAddress = view.findViewById(R.id.rbAddress);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AddressData data = addressDataList.get(position);

        // SETTING ADDRESS
        rbAddress.setText(data.getUserAddress());

        // SETTING ADDRESS TYPE
        if(data.getUserAddressType().equals("Home")){
            rbAddress.setCompoundDrawablesWithIntrinsicBounds(activity.getDrawable(R.drawable.home),null,null,null);
        }
        if(data.getUserAddressType().equals("Work")){
            rbAddress.setCompoundDrawablesWithIntrinsicBounds(activity.getDrawable(R.drawable.work),null,null,null);
        }

        // SETTING PRIMARY ADDRESS CHECKED
        if(infoPreferences.getPrimaryAddressKey().equals(data.getUserAddressKey())){
            rbAddress.setChecked(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Typeface font = activity.getResources().getFont(R.font.mont_regular);
                rbAddress.setTypeface(font);
                rbAddress.setTag("Y");
            }
        }
    }

    @Override
    public int getItemCount() {
        return addressDataList.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ManageAddressAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}
