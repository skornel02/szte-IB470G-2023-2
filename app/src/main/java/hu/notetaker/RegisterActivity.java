package hu.notetaker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_page);


        var email = (EditText) findViewById(R.id.editTextEmail);
        var password = (EditText) findViewById(R.id.editTextPassword);
        var passwordAgain = (EditText) findViewById(R.id.editTextPasswordAgain);
        var registerButton = findViewById(R.id.buttonRegister);

        var mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(v -> {
            var emailAddress = email.getText().toString();
            var passwordText = password.getText().toString();
            var passwordAgainText = passwordAgain.getText().toString();

            if (emailAddress.isEmpty() || passwordText.isEmpty() || passwordAgainText.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordText.equals(passwordAgainText)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(emailAddress, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful! Please log in now.", Toast.LENGTH_LONG).show();
                        this.finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }
}
