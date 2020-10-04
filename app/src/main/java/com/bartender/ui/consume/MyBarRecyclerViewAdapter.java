package com.bartender.ui.consume;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartender.R;
import com.bartender.models.Bar;
import com.bartender.ui.MainActivity;
import com.bartender.ui.consume.BarFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Bar} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyBarRecyclerViewAdapter extends RecyclerView.Adapter<MyBarRecyclerViewAdapter.ViewHolder>
{

    private final List<Bar> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context context;

    public MyBarRecyclerViewAdapter(List<Bar> items, OnListFragmentInteractionListener listener, Context context)
    {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mAddressView.setText(mValues.get(position).getAddress());

        String imgUrl = mValues.get(position).getImage();
        if (imgUrl != null && !imgUrl.isEmpty()) {

            Picasso.get().load(imgUrl).into(holder.mImgView);
        } else {
            holder.mImgView.setImageResource(R.drawable.bar_no_img_sm);
        }
        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView mImgView;
        public final TextView mNameView;
        public final TextView mAddressView;
        public Bar mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.item_name);
            mAddressView = view.findViewById(R.id.address);
            mImgView = view.findViewById(R.id.bar_image);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mAddressView.getText() + "'";
        }
    }

}
