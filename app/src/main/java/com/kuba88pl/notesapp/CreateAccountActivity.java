package com.kuba88pl.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(v -> createAccount());
        loginBtnTextView.setOnClickListener(v -> finish());

    }

    void createAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        boolean isValidated = validateDate(email, password, confirmPassword);
        if (!isValidated) {
            return;
        }

        createAccountinFirebase(email, password);

    }

    void createAccountinFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //creating account is done
                            Utility.showToast(CreateAccountActivity.this, "Pomyślnie utworzono konto! Potwierdź swój adres email");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else {
                            //failure
                            Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateDate(String email, String password, String confirmPassword) {
        // validate the data that are input by user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Nieprawidłowy adres e-mail!");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Hasło powinno zawierać przynajmniej 6 znaków!");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Hasła się nie zgadzają");
            return false;
        }
        return true;
    }
}