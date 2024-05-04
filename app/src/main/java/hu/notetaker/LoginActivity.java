package hu.notetaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);

        var email = (EditText) findViewById(R.id.editTextEmail);
        var password = (EditText) findViewById(R.id.editTextPassword);
        var loginButton = findViewById(R.id.buttonLogin);
        var registerButton = findViewById(R.id.buttonToRegister);

        var mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }

        loginButton.setOnClickListener(v -> {
            var emailAddress = email.getText().toString();
            var passwordText = password.getText().toString();

            if (emailAddress.isEmpty() || passwordText.isEmpty()) {
                Snackbar.make(v, "Please fill in all fields!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                        .show();

                return;
            }

            mAuth.signInWithEmailAndPassword(emailAddress, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // start main activity and close current
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        this.finish();
                    } else {
                        Snackbar.make(v, "Login failed!", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                                .show();
                    }
                });
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
