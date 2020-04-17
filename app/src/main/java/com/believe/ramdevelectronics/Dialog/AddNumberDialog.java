package com.believe.ramdevelectronics.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.believe.ramdevelectronics.R;
import com.believe.ramdevelectronics.VerifyMobile;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddNumberDialog extends BottomSheetDialogFragment {

    private View root;
    private TextInputLayout etPhoneNumberLayout;
    private TextInputEditText etPhoneNumber;
    private TextView tvSendOtp;

    public AddNumberDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.add_number_dialogue, container, false);

        init();
        tvSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etPhoneNumber.getText().toString().isEmpty()
                        && etPhoneNumber.getText().toString().length() == 10){
                    etPhoneNumberLayout.setError("");

                    String mobilenumber = "+91" + etPhoneNumber.getText().toString().trim();
                    Toast.makeText(getActivity(), mobilenumber, Toast.LENGTH_SHORT).show();

                    dismiss();

                    Intent intent = new Intent(getActivity(), VerifyMobile.class);
                    intent.putExtra("mobile",mobilenumber);
                    startActivity(intent);
                }else {
                    etPhoneNumberLayout.setError("Please enter a valid Phone number");
                }
            }
        });

        return root;
    }

    private void init() {

        etPhoneNumberLayout = root.findViewById(R.id.etPhoneNumberLayout);
        etPhoneNumber = root.findViewById(R.id.etPhoneNumber);
        tvSendOtp = root.findViewById(R.id.tvSendOtp);

    }
}
