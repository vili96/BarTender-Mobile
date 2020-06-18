package com.bartender.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bartender.R;
import com.bartender.ui.login.LoginActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity
{
    private static final int splashTimeOut = 3000;
    TextView logo;
    ImageView layout;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        layout = findViewById(R.id.layoutSplash);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(layout);

        Glide.with(this)
                .load(R.drawable.intro)
                .into(imageViewTarget);

        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        logo = findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent;
                if (firebaseAuth.getCurrentUser() != null){
                    intent = new Intent(SplashActivity.this, MainActivity.class);

                }else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                finish();
                startActivity(intent);

            }
        }, splashTimeOut);

        logo.startAnimation(splashAnimation);
    }
}
