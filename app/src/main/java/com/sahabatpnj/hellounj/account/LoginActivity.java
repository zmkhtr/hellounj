package com.sahabatpnj.hellounj.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.sahabatpnj.hellounj.MainActivity;
import com.sahabatpnj.hellounj.R;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Button mLogin;
    private EditText mEmail, mPassword;
    private ProgressBar mLoading;
    private TextView mDaftar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        declareViewId();
        mLoading.setVisibility(View.INVISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Masuk");

        loginUser();
    }

    private void declareViewId() {
        mLoading = findViewById(R.id.progressbarLoginLoading);
        mLogin = findViewById(R.id.btnLogin);
        mEmail = findViewById(R.id.etLoginEmail);
        mPassword = findViewById(R.id.etLoginPassword);
        mDaftar = findViewById(R.id.textLoginToSignUp);
    }

    private void loginUser() {
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();

                if (email.isEmpty()) {
                    mEmail.setError("email tidak boleh kosong");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("mohon gunakan email yang benar, co: mail@email.com");
                } else if (password.isEmpty()) {
                    mPassword.setError("Kata sandi tidak boleh kosong");
                } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && !password.isEmpty()) {
                    proceedLogin(email, password);
                }
            }
        });
        mDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void proceedLogin(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mLoading.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mLoading.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Toast.makeText(LoginActivity.this, "Berhasil masuk",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            mLoading.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Gagal masuk: pastikan kamu menggunakan email dan password yang benar",
                                    Toast.LENGTH_SHORT).show();
                        }
                        mLoading.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
