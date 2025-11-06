package com.example.cineclube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilmesAdapter extends RecyclerView.Adapter<FilmesAdapter.FilmeViewHolder> {

    private final List<String> filmes;

    public FilmesAdapter(List<String> filmes) {
        this.filmes = filmes;
    }

    @NonNull
    @Override
    public FilmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new FilmeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmeViewHolder holder, int position) {
        holder.tvTitulo.setText(filmes.get(position));
    }

    @Override
    public int getItemCount() {
        return filmes.size();
    }

    static class FilmeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;

        public FilmeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(android.R.id.text1);
        }
    }
}
