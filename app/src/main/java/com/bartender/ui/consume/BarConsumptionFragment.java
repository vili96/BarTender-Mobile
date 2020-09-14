package com.bartender.ui.consume;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Class responsible for the creation of consumption in a bar - registered in the system.
 */
public class BarConsumptionFragment extends Fragment
{
    MainActivity mainActivity;
    ConsumedReferenceActivity consumedReferenceActivity;
    //    MainActivity
    private Spinner spinnerDrinkNames;
    private TextView txtAmount;
    private TextView txtPrice;
    private TextView txtAlcVol;
    private EditText etQuantity;
    private TextView txtTotalPrice;
    private ImageView imgDrink;
    private TextView txtBarName;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = firebaseAuth.getUid();
    private boolean editMode = false;
    LoaderDialog loaderDialog;

    private String name = "";
    private double alcVol = 0;
    private double amount = 0;
    private String selectedDrinkId = "-1";
    private double price = 0;
    private int quantity = 0;
    private String barId;
    private String barName;
    private Double amountExtra;
    private Double priceExtra;
    private Double alcVolExtra;
    private Integer quantityExtra;
    private String drinkNameExtra;
    private String consumptionIdExtra;

    List<Drink> drinks = new ArrayList<>();

    private static final String BAR_ID = "bar-id";
    private static final String BAR_NAME = "bar-name";


    // Required empty public constructor
    public BarConsumptionFragment()
    {
    }

    public static BarConsumptionFragment newInstance(String barId, String barName)
    {
        BarConsumptionFragment fragment = new BarConsumptionFragment();
        Bundle args = new Bundle();
        args.putString(BAR_ID, barId);
        args.putString(BAR_NAME, barName);
        fragment.setArguments(args);
        return fragment;
    }

    public static BarConsumptionFragment newInstance(String barId, String barName, double amount, double price, double alcVol, int quantity, String drinkNameExtra, String consumptionIdExtra)
    {
        BarConsumptionFragment fragment = new BarConsumptionFragment();
        Bundle args = new Bundle();
        args.putString(BAR_ID, barId);
        args.putString(BAR_NAME, barName);
        args.putDouble("amount", amount);
        args.putDouble("price", price);
        args.putDouble("alcVol", alcVol);
        args.putInt("quantity", quantity);
        args.putString("drinkNameExtra", drinkNameExtra);
        args.putString("consumptionIdExtra", consumptionIdExtra);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            barId = getArguments().getString(BAR_ID);
            barName = getArguments().getString(BAR_NAME);
            amountExtra = getArguments().getDouble("amount");
            priceExtra = getArguments().getDouble("price");
            alcVolExtra = getArguments().getDouble("alcVol");
            quantityExtra = getArguments().getInt("quantity");
            drinkNameExtra = getArguments().getString("drinkNameExtra");
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
                consumedReferenceActivity.setActionBarTitle("Bar Consumption");
            } else {
                mainActivity = (MainActivity) getActivity();
                if (mainActivity instanceof MainActivity) {
                    loaderDialog = mainActivity.getLoaderDialog();
                    mainActivity.setActionBarTitle("Bar Consumption");
                }
            }
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bar_consumption, container, false);
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
            txtBarName = getView().findViewById(R.id.txtBarName);
            spinnerDrinkNames = getView().findViewById(R.id.etDrinkNames);
            txtAmount = getView().findViewById(R.id.etAmount);
            txtPrice = getView().findViewById(R.id.etPrice);
            txtAlcVol = getView().findViewById(R.id.etAlcVol);
            etQuantity = getView().findViewById(R.id.etQuantity);
            txtTotalPrice = getView().findViewById(R.id.txtTotalPrice);
            imgDrink = getView().findViewById(R.id.imgDrink);
            etQuantity.addTextChangedListener(watcher);

            if (editMode) {
//                txtAlcVol.addTextChangedListener(etWatcher);
//                txtAmount.addTextChangedListener(etWatcher);
//                txtPrice.addTextChangedListener(etWatcher);

                txtAlcVol.setText(String.valueOf(alcVolExtra));
                txtAmount.setText(String.valueOf(amountExtra));
                txtPrice.setText(String.valueOf(priceExtra));
                etQuantity.setText(String.valueOf(quantityExtra));
                txtTotalPrice.setVisibility(View.VISIBLE);

//                txtAlcVol.setEnabled(false);
//                txtAmount.setEnabled(false);
//                txtPrice.setEnabled(false);
                txtBarName.setVisibility(View.GONE);

            } else
                resetTextViews();

            txtBarName.setText(barName);

            Button btnSave = getView().findViewById(R.id.btnSave);
            Button btnCancel = getView().findViewById(R.id.btnCancel);

            db.collection("drinks").whereEqualTo("barId", barId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        drinks.add(document.toObject(Drink.class));
                    }
                }
                if (!drinks.isEmpty()) {
                    List<String> drinkNames = new ArrayList<>();
                    drinkNames.add(0, getString(R.string.spinner_default));
                    drinkNames.addAll(drinks.stream().map(Drink::getName).collect(Collectors.toList()));
                    Context context = mainActivity != null ? mainActivity.getApplicationContext() : consumedReferenceActivity.getApplicationContext();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, drinkNames);


                    spinnerDrinkNames.setAdapter(adapter);
                    spinnerDrinkNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            if (position > 0) {
                                position = position - 1;
                                Drink currentDrink = drinks.get(position);
                                txtAmount.setText(getString(R.string.amount_preselected, currentDrink.getAmount()));
                                txtPrice.setText(getString(R.string.price_predefined, currentDrink.getPrice()));
                                txtAlcVol.setText(getString(R.string.alc_volume_predefined, currentDrink.getAlcVolume()));
                                if (currentDrink.getImage() != null) {
                                    Picasso.get()
                                            .load(currentDrink.getImage())
                                            .into(imgDrink);
                                }

                                name = spinnerDrinkNames.getSelectedItem().toString();
                                alcVol = currentDrink.getAlcVolume();
                                amount = currentDrink.getAmount();
                                price = currentDrink.getPrice();
                                selectedDrinkId = currentDrink.getId();

                                if (ValidationUtils.validateTextField(etQuantity.getText())) {
                                    showTotalPrice();
                                }
                            } else {
                                resetTextViews();
                                txtTotalPrice.setVisibility(View.GONE);
                                imgDrink.setImageResource(R.drawable.def_no_image_drink);
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });

                    if (editMode)
                        spinnerDrinkNames.setSelection(adapter.getPosition(drinkNameExtra));
                    else
                        spinnerDrinkNames.setSelection(0);
                }
            }).addOnFailureListener(failListener -> {
                Toast.makeText(getContext(), "Could not load drinks for that bar!", Toast.LENGTH_LONG).show();
                goBackToMain();
            });

            btnSave.setOnClickListener(onSaveClick);
            btnCancel.setOnClickListener(v -> goBackToMain());
        }
    }

    private View.OnClickListener onSaveClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (spinnerDrinkNames.getSelectedItem() == null || spinnerDrinkNames.getSelectedItemPosition() == 0 ||
                    !ValidationUtils.validateTextField(etQuantity.getText())) {
                Toast.makeText(getContext(), "All fields required!", Toast.LENGTH_LONG).show();
                return;
            }

            if (loaderDialog != null) {
                loaderDialog.showDialog();
            }

            quantity = Integer.parseInt(etQuantity.getText().toString());

            Consumption consumption = new Consumption(editMode ? consumptionIdExtra : DatabaseUtils.generateId(userId), userId, selectedDrinkId, new Date(), quantity, price);
            db.collection("consumptions").document(consumption.getId()).set(consumption).addOnSuccessListener(listenerConsumptions -> {
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
        }
    };

    final TextWatcher etWatcher = new TextWatcher()
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
            if (spinnerDrinkNames.getSelectedItem() == null || spinnerDrinkNames.getSelectedItemPosition() == 0 ||
                    !ValidationUtils.validateTextField(etQuantity.getText())) {
                txtTotalPrice.setVisibility(View.GONE);
            } else {
                showTotalPrice();
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
//            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            consumedReferenceActivity.finish();
        }

    }

    private void resetTextViews()
    {
        txtAlcVol.setText("");
        txtAmount.setText("");
        txtPrice.setText("");
        txtTotalPrice.setVisibility(View.GONE);

    }

    private void showTotalPrice()
    {
        double totalPrice = price * Double.valueOf(etQuantity.getText().toString());
        txtTotalPrice.setText(getString(R.string.total_price, totalPrice));
        txtTotalPrice.setVisibility(View.VISIBLE);
    }

}


