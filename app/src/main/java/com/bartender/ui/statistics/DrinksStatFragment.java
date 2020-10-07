package com.bartender.ui.statistics;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bartender.R;
import com.bartender.models.Drink;

import java.util.ArrayList;
import java.util.List;


public class DrinksStatFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drinks_stat, container, false);
        Resources resource = getContext().getResources();
        TableLayout table = view.findViewById(R.id.eventsTable);
        List<Drink> data = getAllDrinks();
        double totalCost = getTotalCost(data);
        float textSizeH = 20;
        float textSize = 18;

        //create headers
        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(resource.getColor(R.color.colorTableHeader));
        TextView headerCol1 = new TextView(getContext());
        stilyzeTableHeaderColumns(headerCol1, textSizeH, getString(R.string.drink));
        TextView headerCol2 = new TextView(getContext());
        stilyzeTableHeaderColumns(headerCol2, textSizeH, getString(R.string.price));
        TextView headerCol3 = new TextView(getContext());
        stilyzeTableHeaderColumns(headerCol3, textSizeH, getString(R.string.percent));
        headerRow.addView(headerCol1);
        headerRow.addView(headerCol2);
        headerRow.addView(headerCol3);
        table.addView(headerRow);

        //populate table
        for (int i = 0; i < data.size(); i++) {
            Drink drink = data.get(i);
            TableRow rowNext = new TableRow(getContext());
            if (i % 2 == 0) {
                rowNext.setBackgroundColor(resource.getColor(R.color.colorRowEven));
            } else {
                rowNext.setBackgroundColor(resource.getColor(R.color.colorRowOdd));
            }
            TextView activity = new TextView(getContext());
            stilyzeTableColumns(activity, textSize, drink.getName());
            activity.setGravity(Gravity.LEFT);
            activity.setPadding(8, 10, 0, 10);
            TextView cost = new TextView(getContext());
            stilyzeTableColumns(cost, textSize, getString(R.string.actual_cost, drink.getPrice()));
            TextView percent = new TextView(getContext());
            stilyzeTableColumns(percent, textSize, getString(R.string.actual_percent, calculatePercentage(drink.getPrice(), totalCost)));
            rowNext.addView(activity);
            rowNext.addView(cost);
            rowNext.addView(percent);
            table.addView(rowNext);
        }

        //footer
        TableRow footerRow = new TableRow(getContext());
        footerRow.setBackgroundColor(resource.getColor(R.color.colorTableHeader));
        TextView footerCol1 = new TextView(getContext());
        stilyzeTableHeaderColumns(footerCol1, textSizeH, getString(R.string.total));
        TextView footerCol2 = new TextView(getContext());
        stilyzeTableHeaderColumns(footerCol2, textSizeH, getString(R.string.total_cost, totalCost));
        footerRow.addView(footerCol1);
        footerRow.addView(footerCol2);
        table.addView(footerRow);

        return view;
    }

    private List<Drink> getAllDrinks() {
        List<Drink> list = new ArrayList<>();
        list.add(new Drink("a1", "Choco dream", 12, 221, 15, 1));
        list.add(new Drink("a1", "Coco paradise", 12, 221, 5, 1));
        list.add(new Drink("a1", "Ice Raspberry", 12, 221, 6, 1));
        list.add(new Drink("a1", "Peachy", 12, 221, 7, 1));
        list.add(new Drink("a1", "Mojito", 12, 221, 11, 1));
        list.add(new Drink("a1", "Margarita", 12, 221, 15.50, 1));
        list.add(new Drink("a1", "Mai Tai", 12, 221, 5, 1));
        list.add(new Drink("a1", "Strawberry Pie", 12, 221, 6.80, 1));
        list.add(new Drink("a1", "Tiger Eye", 12, 221, 10, 1));
        list.add(new Drink("a1", "Black Russian", 12, 221, 12, 1));

        return list;
    }

    private double calculatePercentage(double currentProfit, double profitSum) {
        return currentProfit * 100 / profitSum;
    }

    private double getTotalCost(List<Drink> data) {
        double costSum = 0;
        for (Drink d : data) {
            costSum += d.getPrice();
        }
        return costSum;
    }

    private void stilyzeTableHeaderColumns(TextView headerCol1, float textSizeH, String text) {
        int lp = 0, tp = 10, rp = 0, bp = 10;
        headerCol1.setText(text);
        headerCol1.setTextColor(Color.WHITE);
        headerCol1.setTextSize(textSizeH);
        headerCol1.setGravity(Gravity.CENTER);
        headerCol1.setPadding(lp, tp, rp, bp);
    }

    private void stilyzeTableColumns(TextView tableCol, float textSize, String text) {
        int lp = 0, tp = 10, rp = 0, bp = 10;
        tableCol.setText(text);
        tableCol.setTextSize(textSize);
        tableCol.setGravity(Gravity.CENTER);
        tableCol.setPadding(lp, tp, rp, bp);
        tableCol.setTypeface(null, Typeface.BOLD);
    }
}
