package com.example.cineclube;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AvaliarAdapter extends RecyclerView.Adapter<AvaliarAdapter.FilmeViewHolder> {

    private Context context;
    private List<Filme> listaFilmes;
    private int idUsuario;
    private String userEmail;

    public AvaliarAdapter(Context context, List<Filme> listaFilmes, int idUsuario, String userEmail) {
        this.context = context;
        this.listaFilmes = listaFilmes;
        this.idUsuario = idUsuario;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public FilmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filme, parent, false);
        return new FilmeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmeViewHolder holder, int position) {
        Filme filme = listaFilmes.get(position);

        holder.tvMovieTitle.setText(filme.getTitulo());
        holder.tvMovieDescription.setText(filme.getDescricao());
        holder.tvMovieYearGenre.setText(filme.getAno() + " • " + filme.getGenero());
        holder.tvMovieRating.setText(String.format("%.1f ★", filme.getNotaMedia()));



        holder.btnRate.setOnClickListener(v -> {
            Intent intent = new Intent(context, AvaliarFilme.class);
            intent.putExtra("id_filme", filme.getId());   // ID do filme
            intent.putExtra("id_usuario", idUsuario);     // ID do usuário logado
            intent.putExtra("filme_titulo", filme.getTitulo());
            intent.putExtra("user_email", userEmail);

            context.startActivity(intent);
        });




    }

    @Override
    public int getItemCount() {
        return listaFilmes.size();
    }

    public static class FilmeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvMovieTitle, tvMovieDescription, tvMovieYearGenre, tvMovieRating;
        Button btnMarkWatched, btnRate;

        public FilmeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieDescription = itemView.findViewById(R.id.tvMovieDescription);
            tvMovieYearGenre = itemView.findViewById(R.id.tvMovieYearGenre);
            tvMovieRating = itemView.findViewById(R.id.tvMovieRating);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}
