package com.bartender.utils;

import java.util.Date;

/**
 * Author: Velina Ilieva
 */
public class DatabaseUtils
{
    public static String generateId(String userId){
        return userId + new Date().getTime();
    }

}
