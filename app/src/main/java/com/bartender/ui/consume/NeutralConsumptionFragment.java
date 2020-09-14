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
import com.bartender.ui.consumedreference.ConsumedReferenceActivity;
import com.bartender.utils.DatabaseUtils;
import com.bartender.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Class responsible for the creation of neutral consumption - not related to any bar from the system.
 */
public class NeutralConsumptionFragment extends Fragment
{
    MainActivity mainActivity;
    ConsumedReferenceActivity consumedReferenceActivity;
    private EditText etDrinkName;
    private EditText etAmount;
    private EditText etPrice;
    private EditText etAlcVol;
    private EditText etQuantity;
    private TextView txtTotalPrice;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = firebaseAuth.getUid();
    private boolean editMode = false;
    LoaderDialog loaderDialog;

    private String name = "";
    private double amount = 0;
    private double price = 0;
    private double alcVol = 0;
    private int quantity = 0;
    private String consumptionIdExtra;

    // Required empty public constructor
    public NeutralConsumptionFragment()
    {
    }

    public static NeutralConsumptionFragment newInstance()
    {
        return new NeutralConsumptionFragment();
    }

    public static NeutralConsumptionFragment newInstance(String name, double amount, double price, double alcVol, int quantity, String consumptionIdExtra)
    {
        NeutralConsumptionFragment fragment = new NeutralConsumptionFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putDouble("amount", amount);
        args.putDouble("price", price);
        args.putDouble("alcVol", alcVol);
        args.putInt("quantity", quantity);
        args.putString("consumptionIdExtra", consumptionIdExtra);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            name = getArguments().getString("name");
            alcVol = getArguments().getDouble("alcVol");
            amount = getArguments().getDouble("amount");
            price = getArguments().getDouble("price");
            quantity = getArguments().getInt("quantity");
            consumptionIdExtra = getArguments().getString("consumptionIdExtra");
        }

        if (getActivity() instanceof ConsumedReferenceActivity) editMode = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (getActivity() != null) {
            if (editMode) {
                consumedReferenceActivity = (ConsumedReferenceActivity) getActivity();
                loaderDialog = consumedReferenceActivity.getLoaderDialog();
                consumedReferenceActivity.setActionBarTitle("Neutral Consumption");
            } else {
                mainActivity = (MainActivity) getActivity();
                loaderDialog = mainActivity.getLoaderDialog();
                mainActivity.setActionBarTitle("Neutral Consumption");
            }
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

            if (editMode) {
                etDrinkName.setText(String.valueOf(name));
                etAmount.setText(String.valueOf(amount));
                etPrice.setText(String.valueOf(price));
                etAlcVol.setText(String.valueOf(alcVol));
                etQuantity.setText(String.valueOf(quantity));
                txtTotalPrice.setVisibility(View.VISIBLE);
            }

            Button btnSave = getView().findViewById(R.id.btnSave);
            Button btnCancel = getView().findViewById(R.id.btnCancel);

            btnSave.setOnClickListener(onSaveClick);
            btnCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    goBackToMain();
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
            db.collection("drinks").document(drink.getId()).set(drink).addOnSuccessListener(aVoid -> {
                Consumption consumption = new Consumption(editMode ? consumptionIdExtra : DatabaseUtils.generateId(userId), userId, drink.getId(), new Date(), quantity, price);
                db.collection("consumptions").document(consumption.getId()).set(consumption).addOnSuccessListener(aVoid1 -> {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        loaderDialog.hideDialog();
                        Toast.makeText(getContext(), editMode ? "Consumption edited!" : "Consumption added!", Toast.LENGTH_LONG).show();
                        goBackToMain();
                    }, 2200);

                }).addOnFailureListener(e -> {
                    loaderDialog.hideDialog();
                    Toast.makeText(getContext(), "Failed to save consumption!", Toast.LENGTH_LONG).show();

                });
            }).addOnFailureListener(e -> {
                loaderDialog.hideDialog();
                Toast.makeText(getContext(), "Failed to save drink!", Toast.LENGTH_LONG).show();
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

    private void goBackToMain()
    {
        assert getFragmentManager() != null;
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (!editMode)
            mainActivity.setActionBarTitle(getResources().getString(R.string.app_name));
        else {
            consumedReferenceActivity.finish();
        }
    }

}


