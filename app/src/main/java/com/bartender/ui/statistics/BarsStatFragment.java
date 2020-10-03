package com.bartender.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bartender.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BarsStatFragment extends Fragment
{
    private List<PieEntry> values;

    public List<PieEntry> getValues()
    {
        return values;
    }

    public void setValues(List<PieEntry> values)
    {
        this.values = values;
    }

    public BarsStatFragment()
    {
    }

    public BarsStatFragment(List<PieEntry> values)
    {
        this.values = values;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_bars_stat, container, false);

        PieChart pieChart = view.findViewById(R.id.pie_chart);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.GRAY);
        pieChart.setExtraOffsets(40.f, 45.f, 40.f, 45.f); // around chart

        PieDataSet pieDataSet = new PieDataSet(values, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(18);
        pieDataSet.setValueTextColor(Color.BLACK);

        pieDataSet.setSliceSpace(3f);
        pieDataSet.setIconsOffset(new MPPointF(0, 40));
        pieDataSet.setSelectionShift(5f);

        // Outside values
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1OffsetPercentage(5f); /** When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size */
        pieDataSet.setValueLinePart1Length(0.8f); /** When valuePosition is OutsideSlice, indicates length of first half of the line */
        pieDataSet.setValueLinePart2Length(0.2f); /** When valuePosition is OutsideSlice, indicates length of second half of the line */

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter()
        {
            @Override
            public String getFormattedValue(float value)
            {
                return String.format(Locale.ENGLISH, "%d", (long) value) + " orders";

            }
        });

        Description descr = new Description();
        descr.setText("Monthly bar orders information");
        descr.setTextSize(16);
        descr.setTextColor(Color.GRAY);
        descr.setYOffset(-90);
//        descr.setPosition(620,790);

        pieChart.setData(pieData);
        pieChart.setDescription(descr);
        pieChart.setHoleRadius(20);
        pieChart.setTransparentCircleRadius(35);
        pieChart.animateXY(800, 800);

        return view;

    }
}
