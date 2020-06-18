package com.bartender.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bartender.R;
import com.bartender.ui.collection.CollectionActivity;
import com.bartender.ui.consume.NeutralConsumptionFragment;
import com.bartender.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    public LoaderDialog getLoaderDialog()
    {
        return loaderDialog;
    }

    LoaderDialog loaderDialog;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AlertDialog consumeDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderDialog = new LoaderDialog(this);

        AlertDialog.Builder consumptionTypeBuilder = new AlertDialog.Builder(this);
        consumptionTypeBuilder.setTitle(R.string.type_selection)
                .setIcon(R.drawable.ic_bar_white_24dp)
                .setPositiveButton(R.string.type_neutral, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, NeutralConsumptionFragment.newInstance()).commit();
                    }
                })
                .setNeutralButton(R.string.type_bars, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });


        consumeDialog = consumptionTypeBuilder.create();
        consumeDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                Button neutralBtn = consumeDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                neutralBtn.setWidth(350);
                neutralBtn.setTextColor(Color.parseColor("#F0F0F0"));
                neutralBtn.setPadding(10,10,10,10);
                neutralBtn.setBackgroundResource(R.drawable.dark_button);


                Button barsBtn = consumeDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                barsBtn.setWidth(350);
                barsBtn.setTextColor(Color.parseColor("#F0F0F0"));
                barsBtn.setPadding(10,10,10,10);
                barsBtn.setBackgroundResource(R.drawable.dark_button);
            }
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

            default:
                // user's action - not recognized.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onMenuItemClick(View v)
    {
        Intent intent;
        if (v.getId() == R.id.consume) {
            consumeDialog.show();
        } else if (v.getId() == R.id.consumedToday) {
            intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.collection) {
            intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        }
    }

}
