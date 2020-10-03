package com.bartender.ui.consumedreference;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartender.R;
import com.bartender.models.Consumption;
import com.bartender.models.Drink;
import com.bartender.ui.LoaderDialog;
import com.bartender.ui.consume.BarConsumptionFragment;
import com.bartender.ui.consume.NeutralConsumptionFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class used as a reference of all created consumptions(neutral and bar consumptions).
 * It provides functionality for editing, deleting, listing and filtering of all consumptions
 * created by the user.
 */
public class ConsumedReferenceActivity extends AppCompatActivity
{
    Menu topMenu;
    RecyclerView gridView;
    LoaderDialog loaderDialog = new LoaderDialog(this);
    Query baseQuery;
    TextView filterLabel;

    FirestoreRecyclerAdapter customAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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

        gridView = findViewById(R.id.consumed_today_container);
        filterLabel = findViewById(R.id.filter_label);

        //---------------------  Firestore Adapter
        loaderDialog.showDialog();
        gridView.setVisibility(View.INVISIBLE);


        // today
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

//                customAdapter.updateOptions(generateOptions(baseQuery.whereGreaterThan("timestamp", today.getTime())));

        baseQuery = db.collection("consumptions").whereEqualTo("userId", firebaseAuth.getUid());
        customAdapter = new FirestoreRecyclerAdapter<Consumption, ConsumptionViewHolder>(generateOptions(baseQuery))
        {
            @Override
            public int getItemCount()
            {
                return super.getItemCount();
            }

            @NonNull
            @Override
            public ConsumptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_consumed_reference, parent, false);
                return new ConsumptionViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ConsumptionViewHolder holder, int position, @NonNull Consumption model)
            {
                db.collection("drinks").document(model.getDrinkId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        Drink currentDrink = documentSnapshot.toObject(Drink.class);

                        assert currentDrink != null;
                        holder.tvName.setText(currentDrink.getName());
                        holder.tvAmount.setText(getString(R.string.total_amount, model.getQuantity() * currentDrink.getAmount()));
                        holder.tvPrice.setText(getString(R.string.total_price, model.getCurrentPrice() * model.getQuantity()));
                        if (currentDrink.getImage() != null) {
                            Picasso.get()
                                    .load(currentDrink.getImage())
                                    .into(holder.ivPic);
                        }

                        holder.ibEdit.setOnClickListener(v -> {
                            if (currentDrink.getBarId() == null)
                                getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content,
                                        NeutralConsumptionFragment.newInstance(currentDrink.getId(), currentDrink.getName(), currentDrink.getAmount(), currentDrink.getPrice(),
                                                currentDrink.getAlcVolume(), model.getQuantity(), model.getId())).commit();
                            else {
                                getSupportFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content,
                                        BarConsumptionFragment.newInstance(currentDrink.getBarId(), "", currentDrink.getAmount(), currentDrink.getPrice(),
                                                currentDrink.getAlcVolume(), model.getQuantity(), currentDrink.getName(), model.getId())).commit();
                            }
                        });
                    }
                });

                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            db.collection("consumptions").document(model.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> { // CASCADE DELETING for custom drinks too
                                        db.collection("drinks").whereEqualTo("id", model.getDrinkId()).get().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    db.collection("drinks").document(documentSnapshot.getId()).delete();
                                                }
                                            }
                                        });
                                        dialog.cancel();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("DELETE", "Error deleting document", e);
                                        dialog.cancel();
                                    });
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.cancel();
                            break;
                    }
                };

                holder.ibDel.setOnClickListener(v -> {
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
            }
        };


        gridView.setAdapter(customAdapter);
        gridView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        gridView.setHasFixedSize(true);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            loaderDialog.hideDialog();
            gridView.setVisibility(View.VISIBLE);
        }, 1500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // add items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.date_filter_menu, menu);
        topMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // today
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        switch (item.getItemId()) {
            case R.id.action_today:
                customAdapter.updateOptions(generateOptions(baseQuery.whereGreaterThan("date", today.getTime())));
//                item.setTitle(Html.fromHtml("<font color='#ff3824'>"+item.getTitle()+"</font>"));
                filterLabel.setText(item.getTitle());
                filterLabel.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_week_ago:
                Calendar weekAgo = today;
                weekAgo.add(Calendar.WEEK_OF_YEAR, -1);
                assert customAdapter != null;
                customAdapter.updateOptions(generateOptions(baseQuery.whereGreaterThan("date", weekAgo.getTime())));
//                item.setTitle(Html.fromHtml("<font color='#ff3824'>"+item.getTitle()+"</font>"));
                filterLabel.setText(item.getTitle());
                filterLabel.setVisibility(View.VISIBLE);

                return true;

            case R.id.action_month_ago:
                Calendar monthAgo = today;
                monthAgo.add(Calendar.MONTH, -1);
                assert customAdapter != null;
                customAdapter.updateOptions(generateOptions(baseQuery.whereGreaterThan("date", monthAgo.getTime())));
                filterLabel.setText(item.getTitle());
                filterLabel.setVisibility(View.VISIBLE);
//                item.setTitle(Html.fromHtml("<font color='#ff3824'>"+item.getTitle()+"</font>"));
                return true;

            case R.id.action_all:
                assert customAdapter != null;
                customAdapter.updateOptions(generateOptions(baseQuery));
                filterLabel.setVisibility(View.GONE);

            default:
                // user's action - not recognized.
                return super.onOptionsItemSelected(item);

        }
    }

    private FirestoreRecyclerOptions<Consumption> generateOptions(Query query)
    {
        return new FirestoreRecyclerOptions.Builder<Consumption>().setQuery(query, Consumption.class).build();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        customAdapter.stopListening();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        customAdapter.startListening();
    }

    private class ConsumptionViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName;
        ImageView ivPic;
        TextView tvAmount;
        TextView tvPrice;
        ImageButton ibEdit;
        ImageButton ibDel;

        public ConsumptionViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_cocktail_name_consumed);
            ivPic = itemView.findViewById(R.id.img_cocktail_consumed);
            tvAmount = itemView.findViewById(R.id.actual_amount);
            tvPrice = itemView.findViewById(R.id.actual_price);
            ibEdit = itemView.findViewById(R.id.btnEdit);
            ibDel = itemView.findViewById(R.id.btnDel);
        }

    }
}
