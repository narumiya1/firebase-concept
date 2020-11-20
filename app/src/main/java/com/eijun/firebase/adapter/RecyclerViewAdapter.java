package com.eijun.firebase.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eijun.firebase.MyListActivity;
import com.eijun.firebase.R;
import com.eijun.firebase.UpdateActivity;
import com.eijun.firebase.model.Mahasiswa;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Mahasiswa> listMahsiswa ;
    private Context context ;

    public interface  dataListener{
        void onDeleteData(Mahasiswa data, int position);
    }

    dataListener listener;


    public RecyclerViewAdapter(ArrayList<Mahasiswa> listMahsiswa, Context context) {
        this.listMahsiswa = listMahsiswa;
        this.context = context;
        listener = (MyListActivity)context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String nim = listMahsiswa.get(position).getNim();
        final String nama = listMahsiswa.get(position).getNama();
        final String jurusan = listMahsiswa.get(position).getJurusan();

        holder.Nim.setText("NIM : "+nim);
        holder.Nama.setText("Name : "+nama);
        holder.Jurusan.setText("Club : "+jurusan);

        holder.listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final String[] action = {"Update","Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Bundle bundle = new Bundle();
                                bundle.putString("dataNIM", listMahsiswa.get(position).getNim());
                                bundle.putString("dataNama", listMahsiswa.get(position).getNama());
                                bundle.putString("dataJurusan", listMahsiswa.get(position).getJurusan());
                                bundle.putString("getPrimaryKey", listMahsiswa.get(position).getKey());

                                Intent intent = new Intent(view.getContext(), UpdateActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;

                            case 1:
                                //Menggunakan interface untuk mengirim data mahasiswa, yang akan dihapus
                                listener.onDeleteData(listMahsiswa.get(position), position );


                                break;


                        }
                    }
                });

                alert.create();
                alert.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMahsiswa.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Nim, Nama, Jurusan ;
        private LinearLayout listItem ;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Nim = itemView.findViewById(R.id.tv_nim);
            Nama = itemView.findViewById(R.id.tv_nama);
            Jurusan = itemView.findViewById(R.id.tv_jurusan);
            listItem = itemView.findViewById(R.id.listz_item);
        }
    }
}
