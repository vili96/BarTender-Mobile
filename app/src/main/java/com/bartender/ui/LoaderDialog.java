package com.bartender.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ImageView;

import com.bartender.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Author: Velina Ilieva
 */
public class LoaderDialog
{
    private Activity activity;
    private Dialog dialog;

    //..we need the context else we can not create the dialog so get context in constructor
    public LoaderDialog(Activity activity)
    {
        this.activity = activity;
    }

    public void showDialog()
    {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading);

        ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

        Glide.with(activity)
                .load(R.drawable.loading_glass)
                .placeholder(R.drawable.loading_glass)
                .fitCenter()
                .into(imageViewTarget);

        dialog.show();
    }

    //hide the dialog when some work is done
    public void hideDialog()
    {
        if (dialog != null) dialog.dismiss();
    }
}
