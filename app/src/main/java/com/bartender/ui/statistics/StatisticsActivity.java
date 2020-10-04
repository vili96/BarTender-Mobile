package com.bartender.ui.statistics;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bartender.R;
import com.bartender.ui.consume.BarFragment;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class StatisticsActivity extends AppCompatActivity
{

    TextView initMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        BottomNavigationView bottomNav = findViewById(R.id.bot_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        initMsg = findViewById(R.id.init_msg_statistics);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            initMsg.setVisibility(View.GONE);

            List<PieEntry> valuesPie = new ArrayList<>();
            valuesPie.add(new PieEntry(5f, "Bushido"));
            valuesPie.add(new PieEntry(10f, "W Garden"));
            valuesPie.add(new PieEntry(2f, "Secrets"));
            getSupportFragmentManager().beginTransaction().addToBackStack(null).add(new BarsStatFragment(valuesPie),"BARS_FRAGMENT").commit();
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {
                case R.id.nav_pie_bars:
                    selectedFragment = new BarsStatFragment(valuesPie);
                    break;
                case R.id.nav_money:
                    List<BarEntry> valuesBar = new ArrayList<>();
                    valuesBar.add(new BarEntry(0, 120));
                    valuesBar.add(new BarEntry(1, 250));
                    valuesBar.add(new BarEntry(2, 80));
                    valuesBar.add(new BarEntry(3, 300));
                    selectedFragment = new MoneyStatFragment(valuesBar);
                    break;
                case R.id.nav_drinks:

                    selectedFragment = new DrinksStatFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}
