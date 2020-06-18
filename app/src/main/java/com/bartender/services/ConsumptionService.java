package com.bartender.services;

import com.bartender.models.Consumption;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;

/**
 * Author: Velina Ilieva
 */
public class ConsumptionService
{
    private static ConsumptionService INSTANCE = null;

    private ConsumptionService()
    {
    }

    public static ConsumptionService getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new ConsumptionService();
        return INSTANCE;
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

//    public Task<DocumentReference> createConsumption( String userId, String drinkId, Date date, int quantity, double currentPrice)
//    {
//
//    }
}
