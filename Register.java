package com.example.northiot.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.northiot.R;
import com.example.northiot.ui.pay.PayActivity;

/**
 * Kayit yapma ekranindan sorumlu kod
 * UI parcalari Register.png dosyasinda bulunabilir
 */
public class RegisterActivity extends AppCompatActivity {
    // Kayit sirasinda kullanici adi ve sifre kontrol mekanizmasi
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText usernameEditText = findViewById(R.id.username);
        final Button registerButton = findViewById(R.id.registerButton);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        registerViewModel.getRegisterFormState().observe(this, registerFormState -> {
            if (registerFormState == null) {
                return;
            }

            // Eger girilen verilerde yazimsal hata yoksa kayit yapmaya izin ver
            registerButton.setEnabled(registerFormState.isDataValid());

            // Eger email yaziminda hata varsa gerekli uyariyi ver
            if (registerFormState.getEmailError() != null) {
                emailEditText.setError(getString(registerFormState.getEmailError()));
            }

            // Eger kullanici adi yaziminda hata varsa gerekli uyariyi ver
            if (registerFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(registerFormState.getUsernameError()));
            }

            // Eger sifre yaziminda hata varsa gerekli uyariyi ver
            if (registerFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(registerFormState.getPasswordError()));
            }
        });

        registerViewModel.getRegisterResult().observe(this, registerResult -> {
            if (registerResult == null) {
                return;
            }

            loadingProgressBar.setVisibility(View.GONE);

            // Kayit yapmada hata varsa uyari ver ve sonlan
            if (registerResult.getError() != null) {
                showRegistrationFailed(registerResult.getError());
                return;
            }

            // Eger basarili olduysa kullaniciya arayuzde geri bildirim ver
            if (registerResult.getSuccess() != null) {
                updateUiWithUser();
            }

            setResult(Activity.RESULT_OK);

            loadingProgressBar.setVisibility(View.VISIBLE);

            // Kayit yapma basarili oldugundan sonraki ekrana gecilmeli:
            // Bu ekrani sonlandir
            finish();
            // Odeme ekranini ac
            startActivity(new Intent(getBaseContext(), PayActivity.class));
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                registerViewModel.registerDataChanged(emailEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        emailEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViewModel.register(emailEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                return true;
            }

            return false;
        });

        registerButton.setOnClickListener(view -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            registerViewModel.register(emailEditText.getText().toString(),
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });
    }

    private void updateUiWithUser() {
        String welcome = "BiPark'a hos geldiniz!";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showRegistrationFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}