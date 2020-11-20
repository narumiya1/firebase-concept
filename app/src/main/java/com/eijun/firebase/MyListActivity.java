package com.eijun.firebase;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eijun.firebase.adapter.RecyclerViewAdapter;
import com.eijun.firebase.model.Mahasiswa;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MyListActivity extends AppCompatActivity implements RecyclerViewAdapter.dataListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    //db reference & arraylist
    private DatabaseReference reference;
    private ArrayList<Mahasiswa> dataMahasiswa;

    //firebase auth
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mylist_data);
        recyclerView = findViewById(R.id.datalist);
        auth = FirebaseAuth.getInstance();
        MyRecyclerView();
        GetData();
    }

    private void GetData() {
        //Mendapatkan Referensi Database
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Admin").child(auth.getUid()).child("Mahasiswa")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Inisialisasi ArrayList
                        dataMahasiswa = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //Mapping data pada DataSnapshot ke dalam objek mahasiswa
                            Mahasiswa mahasiswa = snapshot.getValue(Mahasiswa.class);
                            //Mengambil Primary Key, digunakan untuk proses Update dan Delete
                            mahasiswa.setKey(snapshot.getKey());
                            dataMahasiswa.add(mahasiswa);
                        }

                        //Inisialisasi Adapter dan data Mahasiswa dalam bentuk Array
                        adapter = new RecyclerViewAdapter(dataMahasiswa, MyListActivity.this);

                        //Memasang Adapter pada RecyclerView
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
              /*
                Kode ini akan dijalankan ketika ada error dan
                pengambilan data error tersebut lalu memprint error nya
                ke LogCat
               */
                        Toast.makeText(getApplicationContext(), "Data Gagal Dimuat", Toast.LENGTH_LONG).show();
                        Log.e("MyListActivity", databaseError.getDetails() + " " + databaseError.getMessage());
                    }
                });
    }


    private void MyRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //Membuat Underline pada Setiap Item Didalam List
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.underline));
        recyclerView.addItemDecoration(itemDecoration);

    }

    @Override
    public void onDeleteData(Mahasiswa data, int position) {
        String userId = auth.getUid();
        if (reference != null) {
            reference.child("Admin")
                    .child(userId)
                    .child("Mahasiswa")
                    .child(data.getKey())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MyListActivity.this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}
