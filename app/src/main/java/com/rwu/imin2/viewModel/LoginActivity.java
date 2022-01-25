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

public class LoginActivity extends AppCompatActivity {

    private EditText ETloginEmail;
    private EditText ETloginPassword;
    private Button BTNlogin;
    private Button BTNloginGoToRegister;
    private myUser myUser;
    private FirebaseHandler data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ETloginEmail = findViewById(R.id.ETloginEmail);
        ETloginPassword = findViewById(R.id.ETloginPassword);
        BTNlogin = findViewById(R.id.BTNlogin);
        BTNloginGoToRegister = findViewById(R.id.BTNloginGoToRegister);
        // Initialize Firebasehandler
        data = new FirebaseHandler(LoginActivity.this);

        // onClick login
        BTNlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        // onClick Go to Registration
        BTNloginGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    /**
     * Creates a myUser Object from the input. Checks if fields are filled.
     */
    private void loginUser() {
        String email = ETloginEmail.getText().toString();
        String password = ETloginPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            ETloginEmail.setError("Email darf nicht leer sein");
            ETloginEmail.requestFocus();

        } else if (TextUtils.isEmpty(password)) {
            ETloginPassword.setError("Passwort darf nicht leer sein");
            ETloginPassword.requestFocus();
        } else {
            myUser = new myUser(email, password);
            // Handle Firebase User Login
            data.loginUser(myUser);
        }
    }
}