package com.bartender.utils;

import android.text.Editable;

/**
 * Author: Velina Ilieva
 */
public class ValidationUtils
{
    public static boolean validateTextField(Editable text)
    {
        return text != null && !text.toString().trim().equals("");
    }
}
