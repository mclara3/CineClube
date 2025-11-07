package com.example.cineclube;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilmesAvaliadosAdapter extends RecyclerView.Adapter<FilmesAvaliadosAdapter.FilmeViewHolder> {

    private List<FilmeAvaliado> filmes;
    private SQLiteDatabase db;
    private String userEmail;

    private ActivityResultLauncher<Intent> editRatingLauncher;

    public FilmesAvaliadosAdapter(List<FilmeAvaliado> filmes, SQLiteDatabase db, String userEmail,
                                  ActivityResultLauncher<Intent> editRatingLauncher) {
        this.filmes = filmes;
        this.db = db;
        this.userEmail = userEmail;
        this.editRatingLauncher = editRatingLauncher;
    }


    @NonNull
    @Override
    public FilmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assistido, parent, false);
        return new FilmeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmeViewHolder holder, int position) {
        FilmeAvaliado filme = filmes.get(position);
        holder.tvTitle.setText(filme.getTitulo());
        holder.tvDate.setText(filme.getDataAvaliacao());
        holder.tvRating.setText(filme.getNota() + " ★");
        holder.tvComment.setText(filme.getComentario());

        // Clique no botão Editar → abre AvaliarFilme com dados existentes
        holder.btnEdit.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, AvaliarFilme.class);
            intent.putExtra("id_filme", filme.getIdFilme());
            intent.putExtra("filme_titulo", filme.getTitulo());
            intent.putExtra("id_usuario", filme.getIdUsuario());
            intent.putExtra("nota", filme.getNota());
            intent.putExtra("comentario", filme.getComentario());

            editRatingLauncher.launch(intent);
        });

        // Clique no botão Excluir
        holder.btnDelete.setOnClickListener(v -> {
            db.delete("avaliacoes",
                    "id_filme = (SELECT id_filme FROM filmes WHERE titulo=?) AND id_usuario = (SELECT id_usuario FROM usuarios WHERE email=?)",
                    new String[]{filme.getTitulo(), userEmail});
            Toast.makeText(v.getContext(), "Avaliação excluída!", Toast.LENGTH_SHORT).show();
            filmes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, filmes.size());
        });
    }

    @Override
    public int getItemCount() {
        return filmes.size();
    }

    static class FilmeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvRating, tvComment;
        Button btnEdit, btnDelete;

        public FilmeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvWatchedTitle);
            tvDate = itemView.findViewById(R.id.tvWatchedDate);
            tvRating = itemView.findViewById(R.id.tvWatchedRating);
            tvComment = itemView.findViewById(R.id.tvWatchedComment);
            btnEdit = itemView.findViewById(R.id.btnEditReview);
            btnDelete = itemView.findViewById(R.id.btnDeleteReview);
        }
    }
}
