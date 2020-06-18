package com.bartender.ui.consume;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bartender.R;
import com.bartender.models.Consumption;
import com.bartender.models.Drink;
import com.bartender.ui.LoaderDialog;
import com.bartender.ui.MainActivity;
import com.bartender.utils.DatabaseUtils;
import com.bartender.utils.ValidationUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class NeutralConsumptionFragment extends Fragment
{
    private EditText etDrinkName;
    private EditText etAmount;
    private EditText etPrice;
    private EditText etAlcVol;
    private EditText etQuantity;
    private TextView txtTotalPrice;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = firebaseAuth.getUid();
    LoaderDialog loaderDialog;

    private String name = "";
    private double alcVol = 0;
    private double amount = 0;
    private double price = 0;
    private int quantity = 0;

    // Required empty public constructor
    public NeutralConsumptionFragment()
    {
    }

    public static NeutralConsumptionFragment newInstance()
    {
        return new NeutralConsumptionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (getActivity() != null) {
            loaderDialog = ((MainActivity) getActivity()).getLoaderDialog();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_neutral_consumption, container, false);
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            etDrinkName = getView().findViewById(R.id.etDrinkName);
            etAmount = getView().findViewById(R.id.etAmount);
            etPrice = getView().findViewById(R.id.etPrice);
            etAlcVol = getView().findViewById(R.id.etAlcVol);
            etQuantity = getView().findViewById(R.id.etQuantity);
            txtTotalPrice = getView().findViewById(R.id.txtTotalPrice);

            txtTotalPrice.setVisibility(View.GONE);

            etAmount.addTextChangedListener(watcher);
            etPrice.addTextChangedListener(watcher);
            etAlcVol.addTextChangedListener(watcher);
            etQuantity.addTextChangedListener(watcher);

            Button btnSave = getView().findViewById(R.id.btnSave);
            Button btnCancel = getView().findViewById(R.id.btnCancel);

            btnSave.setOnClickListener(onSaveClick);
            btnCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });
        }


    }

    private View.OnClickListener onSaveClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!ValidationUtils.validateTextField(etDrinkName.getText()) ||
                    !ValidationUtils.validateTextField(etAmount.getText()) ||
                    !ValidationUtils.validateTextField(etPrice.getText()) ||
                    !ValidationUtils.validateTextField(etAlcVol.getText()) ||
                    !ValidationUtils.validateTextField(etQuantity.getText())) {
                Toast.makeText(getContext(), "All fields required!", Toast.LENGTH_LONG).show();
                return;
            }

            if (loaderDialog != null) {
                loaderDialog.showDialog();
            }

            name = etDrinkName.getText().toString();
            alcVol = Double.parseDouble(etAlcVol.getText().toString());
            amount = Double.parseDouble(etAmount.getText().toString());
            price = Double.parseDouble(etPrice.getText().toString());
            quantity = Integer.parseInt(etQuantity.getText().toString());

            final Drink drink = new Drink(DatabaseUtils.generateId(userId), name, alcVol, amount, price, quantity);
            db.collection("drinks").document(drink.getId()).set(drink).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Consumption consumption = new Consumption(DatabaseUtils.generateId(userId), userId, drink.getId(), new Date(), quantity, price);
                    db.collection("consumptions").document(consumption.getId()).set(consumption).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loaderDialog.hideDialog();
                                    Toast.makeText(getContext(), "Consumption added!", Toast.LENGTH_LONG).show();
                                    assert getFragmentManager() != null;
                                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                }
                            }, 2200);

                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            loaderDialog.hideDialog();
                            Toast.makeText(getContext(), "Failed to save consumption!", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    loaderDialog.hideDialog();
                    Toast.makeText(getContext(), "Failed to save drink!", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private final TextWatcher watcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if (!ValidationUtils.validateTextField(etAmount.getText()) ||
                    !ValidationUtils.validateTextField(etPrice.getText()) ||
                    !ValidationUtils.validateTextField(etAlcVol.getText()) ||
                    !ValidationUtils.validateTextField(etQuantity.getText())) {
                txtTotalPrice.setVisibility(View.GONE);
            } else {

                double totalPrice = Double.valueOf(etPrice.getText().toString()) * Double.valueOf(etQuantity.getText().toString());
                txtTotalPrice.setText(getString(R.string.total_price, totalPrice));

                txtTotalPrice.setVisibility(View.VISIBLE);
            }
        }
    };
}


