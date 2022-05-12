package com.example.northiot.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.northiot.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

/**
 * Giris bilgileriyle dogrulama yapmayi ve kullanici bilgilerini elde etmeyi saglayan sinif
 */
public class LoginDataSource {
    private FirebaseAuth mAuth;

    public LoginDataSource() {
        // Firebase Auth'u kur
        mAuth = FirebaseAuth.getInstance();
    }

    public Result<LoggedInUser> login(String email, String password) {
        Task<AuthResult> loginTask = mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LoginDataSource", "signInWithEmail:success");
                    } else {
                        // Eger giris denemesi basarisiz olursa, kullaniciya bir mesaj goster
                        Log.w("LoginDataSource", "signInWithEmail:failure", task.getException());
                    }
                });

        // Giris denemesinin sonlanmasini bekle
        while (loginTask.isComplete() == false);

        if (loginTask.getException() == null && loginTask.getResult() != null) {
            // Eger giris basariliysa yeni bir LoggedInUser yarat ve dondur
            // realUser: Firebase API'nin dondurdugu kullanici
            // mirrorUser: Programin sakladigi ve isledigi kullanici
            FirebaseUser realUser = loginTask.getResult().getUser();
            LoggedInUser mirrorUser = new LoggedInUser(realUser.getUid(), realUser.getDisplayName());
            return new Result.Success<LoggedInUser>(mirrorUser);
        } else {
            return new Result.Error(new IOException("Giris yaparken hata olustu"));
        }
    }

    public Result<LoggedInUser> register(String email, String password) {
        Task<AuthResult> registerTask = mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginDataSource", "createUserWithEmail:success");
                        } else {
                            // Eger kayit denemesi basarisiz olursa, kullaniciya bir mesaj goster
                            Log.w("LoginDataSource", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });

        // Kayit denemesinin sonlanmasini bekle
        while (registerTask.isComplete() == false) ;

        if (registerTask.getException() == null && registerTask.getResult() != null) {
            // Eger kayit basariliysa yeni bir LoggedInUser yarat ve dondur
            // realUser: Firebase API'nin dondurdugu kullanici
            // mirrorUser: Programin sakladigi ve isledigi kullanici
            FirebaseUser realUser = registerTask.getResult().getUser();
            LoggedInUser mirrorUser = new LoggedInUser(realUser.getUid(), realUser.getDisplayName());
            return new Result.Success<LoggedInUser>(mirrorUser);
        } else {
            return new Result.Error(new IOException("Error registering"));
        }
    }
}