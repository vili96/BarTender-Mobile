package com.bartender.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bartender.R;
import com.bartender.models.Bar;
import com.bartender.models.Consumption;
import com.bartender.models.Drink;
import com.bartender.ui.collection.CollectionActivity;
import com.bartender.ui.consume.BarConsumptionFragment;
import com.bartender.ui.consume.BarFragment;
import com.bartender.ui.consume.NeutralConsumptionFragment;
import com.bartender.ui.consumedreference.ConsumedReferenceActivity;
import com.bartender.ui.login.LoginActivity;
import com.bartender.ui.statistics.StatisticsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements BarFragment.OnListFragmentInteractionListener
{
    public LoaderDialog getLoaderDialog()
    {
        return loaderDialog;
    }

    LoaderDialog loaderDialog;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AlertDialog consumeDialog;
    ArrayList<Bar> barsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderDialog = new LoaderDialog(this);

        AlertDialog.Builder consumptionTypeBuilder = new AlertDialog.Builder(this);
        consumptionTypeBuilder.setTitle(R.string.type_selection)
                .setMessage("Please select consumption type")
                .setIcon(R.drawable.ic_bar_white_24dp)
                .setPositiveButton(R.string.type_neutral, (dialog, which) -> getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, NeutralConsumptionFragment.newInstance()).commit())
                .setNeutralButton(R.string.type_bars, (dialog, which) -> db.collection("bars").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loaderDialog.showDialog();
                        barsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            barsList.add(document.toObject(Bar.class));
                        }
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            loaderDialog.hideDialog();
                            getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, BarFragment.newInstance(barsList)).commit();
                        }, 1000);
                    } else {
                        Toast.makeText(getApplicationContext(), "Can't find bars!", Toast.LENGTH_LONG).show();
                    }
                }));

        consumeDialog = consumptionTypeBuilder.create();
        consumeDialog.setOnShowListener(dialog -> {
            Button neutralBtn = consumeDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            neutralBtn.setWidth(350);
            neutralBtn.setTextColor(Color.parseColor("#F0F0F0"));
            neutralBtn.setPadding(10, 7, 10, 7);
            neutralBtn.setBackgroundResource(R.drawable.dark_button);


            Button barsBtn = consumeDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            barsBtn.setWidth(350);
            barsBtn.setTextColor(Color.parseColor("#F0F0F0"));
            barsBtn.setPadding(10, 7, 10, 7);
            barsBtn.setBackgroundResource(R.drawable.dark_button);
        });


//        ActionBar bar = getSupportActionBar();
//        bar.setDisplayShowHomeEnabled(true);
//        bar.setIcon(R.drawable.logout);
//        bar.setDisplayUseLogoEnabled(true);
//
//        Menu myToolbar = (Menu) findViewById(R.menu.top_menu);
//        setSupportActionBar(myToolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // add items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_logout:
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);

                return true;
            case R.id.action_profile:

                //TODO: profile fragment for password editing
                return true;

            default:
                // user's action - not recognized.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loaderDialog.hideDialog();
    }

    public void onMenuItemClick(View v)
    {
        Intent intent;
        if (v.getId() == R.id.consume) {
            consumeDialog.show();
        } else if (v.getId() == R.id.consumedToday) {
//            loaderDialog.showDialog();
            ArrayList<Drink> consumedDrinks = new ArrayList<>();

            ArrayList<Consumption> consumptions = new ArrayList<>();
            db.collection("consumptions").whereEqualTo("userId", firebaseAuth.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    ArrayList<String> drinkIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Consumption consumption = document.toObject(Consumption.class);
                        consumptions.add(consumption);
                        drinkIds.add(consumption.getDrinkId());
                    }

                    Intent consumedIntent = new Intent(getApplicationContext(), ConsumedReferenceActivity.class);

                    if (task.getResult().isEmpty())
                        startActivity(consumedIntent); // NO CONSUMPTIONS FOR THE USER
                    else {
                        //get the drink

//                    DocumentReference docRef = db.collection("drinks").document(consumption.getDrinkId());
//                    docRef.addSnapshotListener()


                        db.collection("drinks").whereIn("id", drinkIds).get().addOnCompleteListener(taskDrinks -> {
                            if (taskDrinks.getResult() != null) {
                                for (DocumentSnapshot documentSnapshot : taskDrinks.getResult().getDocuments()) {
                                    consumedDrinks.add(documentSnapshot.toObject(Drink.class));
                                }

                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("consumptions", consumptions);
                                bundle.putParcelableArrayList("consumedDrinks", consumedDrinks);
                                consumedIntent.putExtras(bundle);
//                                final Handler handler = new Handler();
//                                handler.postDelayed(() -> {
//                                    loaderDialog.hideDialog();
                                    startActivity(consumedIntent);
//                                }, 1000);
                            }
                        });
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Can't find bars!", Toast.LENGTH_LONG).show();
                }
            });

        } else if (v.getId() == R.id.collection) {
            intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        }
    }

    public void setActionBarTitle(String title)
    {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    public void onListFragmentInteraction(Bar bar)
    {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, BarConsumptionFragment.newInstance(bar.getId(), bar.getName())).commit();
    }
}
