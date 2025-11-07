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

public class FilmesAdapterMain extends RecyclerView.Adapter<FilmesAdapterMain.FilmeViewHolder> {

    private final Context context;
    private final List<Filme> filmes;
    private final int idUsuario; // ✅ Adicionado para saber qual usuário está logado

    private final String currentUserEmail;

    public FilmesAdapterMain(Context context, List<Filme> filmes, int idUsuario, String currentUserEmail) {
        this.context = context;
        this.filmes = filmes;
        this.idUsuario = idUsuario;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public FilmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filme, parent, false);
        return new FilmeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmeViewHolder holder, int position) {
        Filme filme = filmes.get(position);

        holder.tvTitle.setText(filme.getTitulo());
        holder.tvYearGenre.setText(filme.getGenero() + " | " + filme.getAno());
        holder.tvDescription.setText(filme.getDescricao());
        holder.tvRating.setText(String.format("%.1f ★", filme.getNotaMedia()));

        // Define o pôster conforme o nome do filme
        switch (filme.getTitulo()) {
            case "A Origem":
                holder.ivPoster.setImageResource(R.drawable.origin);
                break;
            case "Clube da Luta":
                holder.ivPoster.setImageResource(R.drawable.club);
                break;
            case "Forrest Gump":
                holder.ivPoster.setImageResource(R.drawable.gump);
                break;
            case "Interestelar":
                holder.ivPoster.setImageResource(R.drawable.inter);
                break;
            case "O Poderoso Chefão":
                holder.ivPoster.setImageResource(R.drawable.podchef);
                break;
            default:
                holder.ivPoster.setImageResource(R.drawable.ic_poster_placeholder);
                break;
        }

        // 👉 Clique no card para abrir detalhes
        holder.cardMovie.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesFilmeActivity.class);
            intent.putExtra("id_filme", filme.getId());
            intent.putExtra("user_email", currentUserEmail);
            context.startActivity(intent);
        });

        // ✅ Clique do botão "Avaliar"
        holder.btnRate.setOnClickListener(v -> {
            Intent intent = new Intent(context, AvaliarFilme.class);
            intent.putExtra("id_filme", filme.getId());
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("filme_titulo", filme.getTitulo());
            intent.putExtra("user_email", currentUserEmail);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filmes.size();
    }

    public static class FilmeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvYearGenre, tvDescription, tvRating;
        Button btnRate;
        androidx.cardview.widget.CardView cardMovie;

        public FilmeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvYearGenre = itemView.findViewById(R.id.tvMovieYearGenre);
            tvDescription = itemView.findViewById(R.id.tvMovieDescription);
            tvRating = itemView.findViewById(R.id.tvMovieRating);
            btnRate = itemView.findViewById(R.id.btnRate);
            cardMovie = itemView.findViewById(R.id.card_movie);
        }
    }
}
