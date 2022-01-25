package com.rwu.imin2.viewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rwu.imin2.R;
import com.rwu.imin2.model.FirebaseHandler;
import com.rwu.imin2.model.myUser;

public class RegisterActivity extends AppCompatActivity {


    private EditText ETregisterEmail;
    private EditText ETregisterPassword;
    private Button BTNregister;
    private Button BTNregisterGoToLogin;
    private EditText ETregisterUsername;
    private myUser myUser;
    private FirebaseHandler data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ETregisterEmail = findViewById(R.id.ETregisterEmail);
        ETregisterPassword = findViewById(R.id.ETregisterPassword);
        BTNregister = findViewById(R.id.BTNregister);
        BTNregisterGoToLogin = findViewById(R.id.BTNregisterGoToLogin);
        ETregisterUsername = findViewById(R.id.ETregisterUsername);
        // Initialize Firebasehandler
        data = new FirebaseHandler(RegisterActivity.this);

        // onClick register
        BTNregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
        // onClick Go to Login
        BTNregisterGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    /**
     * Creates a myUser Object from the input. Checks if fields are filled.
     */
    private void createUser() {
        String email = ETregisterEmail.getText().toString();
        String password = ETregisterPassword.getText().toString();
        String username = ETregisterUsername.getText().toString();

        if (TextUtils.isEmpty(email)) {
            ETregisterEmail.setError("Email darf nicht leer sein");
            ETregisterEmail.requestFocus();

        } else if (TextUtils.isEmpty(password)) {
            ETregisterPassword.setError("Passwort darf nicht leer sein");
            ETregisterPassword.requestFocus();
        } else if (TextUtils.isEmpty(username)) {
            ETregisterUsername.setError("Benutzername darf nicht leer sein");
            ETregisterUsername.requestFocus();
        } else {
            myUser = new myUser();
            myUser.setEmail(email);
            myUser.setPassword(password);
            myUser.setDisplayName(username);

            // Handle Firebase User Registration
            data.registerUser(myUser);
            // Navigate to Login Screen after registration
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
    }


}