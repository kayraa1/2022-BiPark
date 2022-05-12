package com.example.northiot.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
 * Giris yapma ekranindan sorumlu kod
 * UI parcalari Login.png dosyasinda bulunabilir
 */
public class LoginActivity extends AppCompatActivity {
    // Giris sirasinda kullanici adi ve sifre kontrol mekanizmasi
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.loginButton);
        final Button registerViewButton = findViewById(R.id.registerViewButton);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }

            // Eger girilen verilerde yazimsal hata yoksa giris yapmaya izin ver
            loginButton.setEnabled(loginFormState.isDataValid());

            // Eger email yaziminda hata varsa gerekli uyariyi ver
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }

            // Eger sifre yaziminda hata varsa gerekli uyariyi ver
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }

            loadingProgressBar.setVisibility(View.GONE);

            // Giris yapmada hata varsa uyari ver ve sonlan
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
                Log.w("LoginActivity", "login:failure");
                return;
            }

            // Eger basarili olduysa kullaniciya arayuzde geri bildirim ver
            if (loginResult.getSuccess() != null) {
                updateUiWithUser();
            }

            setResult(Activity.RESULT_OK);

            // Giris yapma basarili oldugundan sonraki ekrana gecilmeli:
            // Bu ekrani sonlandir
            finish();
            // Odeme ekranini ac
            startActivity(new Intent(getBaseContext(), PayActivity.class));
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
                return true;
            }

            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        registerViewButton.setOnClickListener(view -> {
            loadingProgressBar.setVisibility(View.VISIBLE);

            // Kayit olmak istendiginden sonraki ekrana gecilmeli:
            // Bu ekrani sonlandir
            finish();
            // Kayit ekranini ac
            startActivity(new Intent(view.getContext(), RegisterActivity.class));
        });
    }

    private void updateUiWithUser() {
        String welcome = "Tekrar hos geldiniz";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}