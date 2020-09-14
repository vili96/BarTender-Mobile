package com.bartender.ui.consumedreference;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartender.R;
import com.bartender.models.Consumption;
import com.bartender.models.Drink;
import com.bartender.ui.LoaderDialog;
import com.bartender.ui.consume.BarConsumptionFragment;
import com.bartender.ui.consume.NeutralConsumptionFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Class used as a reference of all created consumptions(neutral and bar consumptions).
 * It provides functionality for editing, deleting, listing and filtering of all consumptions
 * created by the user.
 */
public class ConsumedReferenceActivity extends AppCompatActivity
{
    GridView gridView;
    List<Consumption> consumptions = new ArrayList<>();
    Map<String, Drink> consumedDrinkMap = new HashMap<>();
    LoaderDialog loaderDialog = new LoaderDialog(this);

    CustomAdapter customAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LoaderDialog getLoaderDialog()
    {
        return loaderDialog;
    }

    public void setActionBarTitle(String title)
    {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumed_reference);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ArrayList<Consumption> extras = bundle.getParcelableArrayList("consumptions");
            if (extras != null) consumptions.addAll(extras);
            ArrayList<Drink> drinks = bundle.getParcelableArrayList("consumedDrinks");
            if (drinks != null) fillConsumedDrinksMap(consumptions, drinks);

        }

        gridView = findViewById(R.id.consumed_today_container);
        customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    private void fillConsumedDrinksMap(List<Consumption> consumptions, ArrayList<Drink> drinks)
    {
        Map<String, Drink> drinksMap = new HashMap<>();
        for (Drink drink : drinks) {
            drinksMap.put(drink.getId(), drink);
        }

        for (Consumption consumption : consumptions) {
            consumedDrinkMap.put(consumption.getId(), drinksMap.get(consumption.getDrinkId()));
        }
    }


    private class CustomAdapter extends BaseAdapter
    {
        TextView tvName;
        ImageView ivPic;
        TextView tvAmount;
        TextView tvPrice;
        ImageButton ibEdit;
        ImageButton ibDel;

        @Override
        public int getCount()
        {
            return consumptions.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View consumptionView = getLayoutInflater().inflate(R.layout.row_consumed_reference, null);


            final Consumption currentConsumption = consumptions.get(position);

            final Drink currentDrink = consumedDrinkMap.get(currentConsumption.getId());

            assert currentDrink != null;
            tvName = consumptionView.findViewById(R.id.tv_cocktail_name_consumed);
            ivPic = consumptionView.findViewById(R.id.img_cocktail_consumed);
            tvAmount = consumptionView.findViewById(R.id.actual_amount);
            tvPrice = consumptionView.findViewById(R.id.actual_price);

            tvName.setText(currentDrink.getName());
            tvAmount.setText(getString(R.string.total_amount, currentConsumption.getQuantity() * currentDrink.getAmount()));
            tvPrice.setText(getString(R.string.total_price, currentConsumption.getCurrentPrice() * currentConsumption.getQuantity()));
            if (currentDrink.getImage() != null) {
                Picasso.get()
                        .load(currentDrink.getImage())
                        .into(ivPic);
            }

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        db.collection("consumptions").document(currentConsumption.getId()).delete();
                        dialog.cancel();
                        customAdapter.removeItem(position);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.cancel();
                        break;
                }
            };

            ibEdit = consumptionView.findViewById(R.id.btnEdit);
            ibEdit.setOnClickListener(v -> {
                if (currentDrink.getBarId() == null)
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content,
                            NeutralConsumptionFragment.newInstance(currentDrink.getName(), currentDrink.getAmount(), currentDrink.getPrice(),
                                    currentDrink.getAlcVolume(), currentConsumption.getQuantity(), currentConsumption.getId())).commit();
                else {
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content,
                            BarConsumptionFragment.newInstance(currentDrink.getBarId(), "", currentDrink.getAmount(), currentDrink.getPrice(),
                                    currentDrink.getAlcVolume(), currentConsumption.getQuantity(), currentDrink.getName(), currentConsumption.getId())).commit();
                }
            });

            ibDel = consumptionView.findViewById(R.id.btnDel);
            ibDel.setOnClickListener(v -> {


                AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(ConsumedReferenceActivity.this);
                deleteDialogBuilder.setTitle("Delete consumption");
                deleteDialogBuilder.setMessage("Are you sure?");
                deleteDialogBuilder.setCancelable(true);

                deleteDialogBuilder.setPositiveButton(
                        "Yes",
                        dialogClickListener);

                deleteDialogBuilder.setNegativeButton(
                        "No",
                        dialogClickListener);

                AlertDialog alertDel = deleteDialogBuilder.create();
                alertDel.show();

            });


            return consumptionView;
        }

        void removeItem(int position)
        {
            consumptions.remove(position);
            notifyDataSetChanged();
        }
    }
}
