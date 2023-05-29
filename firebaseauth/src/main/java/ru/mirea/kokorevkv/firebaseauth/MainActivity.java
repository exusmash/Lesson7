package ru.mirea.kokorevkv.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.kokorevkv.firebaseauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        binding.btnCreateAccount.setOnClickListener(v -> {
            if (binding.btnCreateAccount.getText().toString() == getString(R.string.create_account)){
                createAccount(binding.editEmail.getText().toString(), binding.editPassword.getText().toString());
            } else {
                sendEmailVerification();
            }

        });
        binding.btnSignIn.setOnClickListener(v -> {
            if(binding.btnSignIn.getText().toString() == getString(R.string.sign_out)){
                signOut();
            }
        });
    }

    private void sendEmailVerification() {
        binding.btnCreateAccount.setEnabled(false);
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.btnCreateAccount.setEnabled(true);
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Verification email sent to" + user.getEmail(), Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification" + task.getException());
                            Toast.makeText(MainActivity.this, "Failed to send Verification email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signOut() {
        firebaseAuth.signOut();
        updateUI(null);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if(! validateForm(email)){
            Log.d(TAG, "Invalid email form");
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createAccount is successful");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "createAccount is fail", task.getException());
                            Toast.makeText(MainActivity.this, "account creation failed", Toast.LENGTH_LONG).show();
                            updateUI(null);

                        }
                    }
                });
    }

    private boolean validateForm(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null){
            binding.textEmailAndPassword.setText(getString(R.string.emailpassword_status_fmt, firebaseUser.getEmail(), firebaseUser.isEmailVerified()));
            binding.textSigned.setText(getString(R.string.firebase_status_fmt, firebaseUser.getUid()));
            binding.editEmail.setVisibility(View.GONE);
            binding.editPassword.setVisibility(View.GONE);
            binding.btnSignIn.setText(getString(R.string.sign_out));
            binding.btnSignIn.setVisibility(View.VISIBLE);
            binding.btnCreateAccount.setText(getText(R.string.verify_email));
        } else {
            binding.btnSignIn.setText(getString(R.string.sign_in));
            binding.btnCreateAccount.setText(getText(R.string.create_account));
            binding.textEmailAndPassword.setText(R.string.signed_out);
            binding.textSigned.setText(null);
            binding.editEmail.setVisibility(View.VISIBLE);
            binding.editPassword.setVisibility(View.VISIBLE);
        }
    }
}