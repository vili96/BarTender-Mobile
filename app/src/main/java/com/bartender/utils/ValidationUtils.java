package com.bartender.utils;

import android.text.Editable;

import io.opencensus.internal.StringUtils;

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
