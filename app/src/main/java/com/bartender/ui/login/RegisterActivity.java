package com.bartender.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bartender.R;
import com.bartender.services.UserService;
import com.bartender.ui.MainActivity;
import com.bartender.utils.ValidationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity
{
    UserService userService = UserService.getInstance();
    EditText email;
    EditText password;
    EditText passwordRepeated;
    TextView loginLink;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordRepeated = findViewById(R.id.passwordRepeated);
        firebaseAuth = FirebaseAuth.getInstance();
        loginLink = findViewById(R.id.loginLink);

        loginLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
//                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(intent);
            }
        });
    }

    public void onRegisterClick(View v)
    {
        if (!ValidationUtils.validateTextField(password.getText()) ||
                !ValidationUtils.validateTextField(email.getText()) ||
                !ValidationUtils.validateTextField(passwordRepeated.getText())) {

            Toast.makeText(RegisterActivity.this, "All fields required!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.getText().toString().length() < 6) {

            Toast.makeText(RegisterActivity.this, "The password should contain minimum 6 symbols", Toast.LENGTH_LONG).show();
            return;
        }


        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())

                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful()) {
                    userService.createUser(firebaseAuth.getUid(), email.getText().toString());
                    Intent mainPage = new Intent(RegisterActivity.this, MainActivity.class);
                    finish();
                    Toast.makeText(RegisterActivity.this, "User successfully registered!", Toast.LENGTH_LONG).show();
                    startActivity(mainPage);
                    LoginActivity.activity.finish(); // finish login activity on registration
                } else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
