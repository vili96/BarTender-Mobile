package com.bartender.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bartender.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MoneyStatFragment extends Fragment
{
    private List<BarEntry> values;

    public List<BarEntry> getValues()
    {
        return values;
    }

    public void setValues(List<BarEntry> values)
    {
        this.values = values;
    }

    public MoneyStatFragment(List<BarEntry> values)
    {
        this.values = values;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_money_stat, container, false);

        BarChart barChart = view.findViewById(R.id.bar_chart);

        BarDataSet barDataSet = new BarDataSet(values, "Months");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextSize(18);

        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new ValueFormatter()
        {
            @Override
            public String getFormattedValue(float value)
            {
                return "$ " + value;
            }
        });

        Description descr = new Description();
        descr.setText("");

        barChart.setData(barData);
        barChart.animateXY(800, 800);
        barChart.setDescription(descr);

        // XAxis setup
        XAxis xAxis = barChart.getXAxis();
        xAxis.setAxisMaximum(4); // last index of the x axis objects
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true); // start from first element
        xAxis.setValueFormatter(new ValueFormatter()
        {
            @Override
            public String getFormattedValue(float value)
            {
                ArrayList<String> xAxisVals = getXAxisValues();
                return xAxisVals.get((int) value);
            }
        });

        //YAxisLeft
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMaximum(600);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setValueFormatter(new ValueFormatter()
        {
            @Override
            public String getFormattedValue(float value)
            {
                return "$ " + value;
            }
        });

        //remove YAxisRight
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        return view;
    }

    private ArrayList<String> getXAxisValues()
    {
        ArrayList<String> xAxisVals = new ArrayList<>();
        xAxisVals.add("JAN");
        xAxisVals.add("FEB");
        xAxisVals.add("MAR");
        xAxisVals.add("APR");
        xAxisVals.add("MAY");
        return xAxisVals;
    }

    private ArrayList<String> getYAxisValues()
    {
        ArrayList<String> yAxisVals = new ArrayList<>();
        yAxisVals.add("$ 0");
        yAxisVals.add("$ 20,000");
        yAxisVals.add("$ 40,000");
        yAxisVals.add("$ 60,000");
        yAxisVals.add("$ 80,000");
        yAxisVals.add("$ 100,000");
        yAxisVals.add("$ 100,000");
        yAxisVals.add("$ 100,000");
        yAxisVals.add("$ 100,000");
        return yAxisVals;
    }

}
