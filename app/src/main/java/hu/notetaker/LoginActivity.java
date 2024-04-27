package hu.notetaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);

        var email = (EditText) findViewById(R.id.editTextEmail);
        var password = (EditText) findViewById(R.id.editTextPassword);
        var loginButton = findViewById(R.id.buttonLogin);
        var registerButton = findViewById(R.id.buttonToRegister);

        var mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            var emailAddress = email.getText().toString();
            var passwordText = password.getText().toString();

            if (emailAddress.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
