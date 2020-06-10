package com.bartender.services;

import com.bartender.models.User;
import com.bartender.utils.AppConstants;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Author: Velina Ilieva
 */
public class UserService
{
    private static UserService INSTANCE = null;

    private UserService() {}

    public static UserService getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new UserService();
        return INSTANCE;
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void createUser(String id, String email)
    {
        User user = new User(id, email, AppConstants.RoleConstants.USER);
        db.collection("Users").document(user.getId()).set(user);
    }

}
