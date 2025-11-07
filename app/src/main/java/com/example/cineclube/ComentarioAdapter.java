package com.example.cineclube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ViewHolder> {

    private List<Comentario> comentarios;

    public ComentarioAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);

        holder.tvUserName.setText(comentario.getNomeUsuario());
        holder.tvComment.setText(comentario.getComentario());

        holder.tvRating.setText(String.format("%.1f ★", comentario.getNota()));
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvComment, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
