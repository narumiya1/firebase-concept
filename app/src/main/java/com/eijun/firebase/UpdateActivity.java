package com.eijun.firebase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eijun.firebase.model.Mahasiswa;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateActivity extends AppCompatActivity {

    private EditText nimBaru, namaBaru, jurusanBaru ;
    private Button update ;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private String cekNim, cekNama, cekJurusan ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        nimBaru = findViewById(R.id.new_nim);
        namaBaru = findViewById(R.id.new_nama);
        jurusanBaru = findViewById(R.id.new_jurusan);
        update = findViewById(R.id.update);

        //Mendapatkan Instance autentikasi dan Referensi dari Database
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekNim = nimBaru.getText().toString();
                cekNama = namaBaru.getText().toString();
                cekJurusan = jurusanBaru.getText().toString();

                if (isEmpty(cekNim) || isEmpty(cekNama) || isEmpty(cekJurusan)){
                    Toast.makeText(UpdateActivity.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();

                }else {
                    Mahasiswa setMahasiswa = new Mahasiswa();
                    setMahasiswa.setNim(nimBaru.getText().toString());
                    setMahasiswa.setNama(namaBaru.getText().toString());
                    setMahasiswa.setJurusan(jurusanBaru.getText().toString());
                    updateMahasiswa(setMahasiswa);

                }

            }
        });

    }

    private void updateMahasiswa(Mahasiswa setMahasiswa) {

        String userId = auth.getUid();
        String getKey = getIntent().getExtras().getString("getPrimaryKey");

        databaseReference.child("Admin")
                .child(userId)
                .child("Mahasiswa")
                .child(getKey)
                .setValue(setMahasiswa)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nimBaru.setText("");
                        namaBaru.setText("");
                        jurusanBaru.setText("");
                        Toast.makeText(UpdateActivity.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void getData() {
        final String getNim = getIntent().getExtras().getString("dataNIm");
        final String getNama = getIntent().getExtras().getString("dataNama");
        final String getJurusan = getIntent().getExtras().getString("dataJurusan");
        nimBaru.setText(getNim);
        namaBaru.setText(getNama);
        jurusanBaru.setText(getJurusan);


    }

    private boolean isEmpty(String s) {

        return TextUtils.isEmpty(s);
    }
}
