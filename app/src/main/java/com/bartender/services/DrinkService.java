package com.bartender.services;

import com.bartender.models.Drink;
import com.bartender.utils.DatabaseUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Author: Velina Ilieva
 */
public class DrinkService
{

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static DrinkService INSTANCE = null;

    private DrinkService()
    {
    }

    public static DrinkService getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new DrinkService();
        return INSTANCE;
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Drink createDrink(String name, double alcVolume, double amount, double price, int ordersCount)
    {
        Drink drink = new Drink(DatabaseUtils.generateId(firebaseAuth.getUid()), name, alcVolume, amount, price, ordersCount);
        return drink;

    }

    public Task<DocumentSnapshot> getDrinkById(String id)
    {
        Task<DocumentSnapshot> task = db.collection("Drinks").document(id).get();

        return task;
    }
}
