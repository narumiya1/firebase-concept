package com.eijun.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eijun.firebase.model.Mahasiswa;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;

    private EditText NIM, NAMA, JURUSAN;
    private FirebaseAuth auth;
    private Button Logout, Simpan, Login, Showdata;

    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logout = findViewById(R.id.logout);
        Logout.setOnClickListener(this);
        Simpan = findViewById(R.id.save);
        Simpan.setOnClickListener(this);
        Login = findViewById(R.id.login);
        Login.setOnClickListener(this);
        Showdata= findViewById(R.id.showdata);
        Showdata.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progress);
        NIM = findViewById(R.id.nim);
        NAMA = findViewById(R.id.nama);
        JURUSAN = findViewById(R.id.jurusan);

        if (auth.getCurrentUser() == null) {
            defaultUi();
        } else {
            updateUI();
        }
    }

    private void defaultUi() {
        Logout.setEnabled(false);
        Simpan.setEnabled(false);
        Showdata.setEnabled(false);
        Login.setEnabled(true);
        NIM.setEnabled(false);
        NAMA.setEnabled(false);
        JURUSAN.setEnabled(false);

    }

    private void updateUI() {
        Logout.setEnabled(true);
        Simpan.setEnabled(true);
        Login.setEnabled(false);
        Showdata.setEnabled(true);
        NIM.setEnabled(true);
        NAMA.setEnabled(true);
        JURUSAN.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Login Canclled", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        //Memilih Provider atau Method masuk yang akan d gunakan
                        //menggunakan provider Google
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false)
                        .build(), RC_SIGN_IN);
//                progressBar.setVisibility(View.VISIBLE);
                break;

            case R.id.save:

                String getUserID = auth.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getReference;

                String getNim = NIM.getText().toString();
                String getNama = NAMA.getText().toString();
                String getJurusan = JURUSAN.getText().toString();

                // Mendapatkan Referensi dari Database
                getReference = database.getReference();

                if (isEmpty(getNim) && isEmpty(getNama) && isEmpty(getJurusan)) {
                       Toast.makeText(MainActivity.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                } else {
                    getReference.child("Admin").child(getUserID).child("Mahasiswa").push()
                            .setValue(new Mahasiswa(getNim, getNama, getJurusan))
                            .addOnSuccessListener(this, new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    NIM.setText("");
                                    NAMA.setText("");
                                    JURUSAN.setText("");
                                    Toast.makeText(MainActivity.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();

                                }
                            });
                }

                break;

            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "Logout Sucessfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                break;

            case R.id.showdata:
                startActivity(new Intent(MainActivity.this, MyListActivity.class));
                break;

        }

    }
}