package com.example.cineclube;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DetalhesFilmeActivity extends BaseActivity {

    private ImageView imgPoster;
    private TextView txtTituloFilme, txtDescricaoFilme, txtNota;
    private RatingBar ratingFilme;
    private RecyclerView rvComentarios;

    private DatabaseHelper dbHelper;
    private int idFilme;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhefilmes);

        Intent intent = getIntent();
        idFilme = intent.getIntExtra("id_filme", -1);
        currentUserEmail = intent.getStringExtra("user_email");

        imgPoster = findViewById(R.id.imgPoster);
        txtTituloFilme = findViewById(R.id.txtTituloFilme);
        txtDescricaoFilme = findViewById(R.id.txtDescricaoFilme);
        txtNota = findViewById(R.id.txtNota);
        ratingFilme = findViewById(R.id.ratingFilme);
        rvComentarios = findViewById(R.id.rvComentarios);

        dbHelper = new DatabaseHelper(this);

        setupBottomNav(-1, currentUserEmail);

        if (idFilme != -1) {
            carregarDetalhesFilme();
            carregarComentarios();
        }
    }

    private void carregarDetalhesFilme() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT f.titulo, f.descricao, COALESCE(AVG(a.nota),0) AS nota_media " +
                        "FROM filmes f LEFT JOIN avaliacoes a ON f.id_filme=a.id_filme " +
                        "WHERE f.id_filme=? GROUP BY f.titulo, f.descricao",
                new String[]{String.valueOf(idFilme)}
        );

        if (cursor.moveToFirst()) {
            String titulo = cursor.getString(0);
            String descricao = cursor.getString(1);
            float notaMedia = cursor.getFloat(2);

            txtTituloFilme.setText(titulo);
            txtDescricaoFilme.setText(descricao);
            txtNota.setText(String.format("%.1f/5", notaMedia));
            ratingFilme.setRating(notaMedia);

            switch (titulo) {
                case "A Origem": imgPoster.setImageResource(R.drawable.origin); break;
                case "Clube da Luta": imgPoster.setImageResource(R.drawable.club); break;
                case "Forrest Gump": imgPoster.setImageResource(R.drawable.gump); break;
                case "Interestelar": imgPoster.setImageResource(R.drawable.inter); break;
                case "O Poderoso Chefão": imgPoster.setImageResource(R.drawable.podchef); break;
                default: imgPoster.setImageResource(R.drawable.ic_poster_placeholder); break;
            }
        }

        cursor.close();
        db.close();
    }

    private void carregarComentarios() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.nome, c.comentario, a.nota " +
                        "FROM comentarios c INNER JOIN usuarios u ON u.id_usuario=c.id_usuario " +
                        "LEFT JOIN avaliacoes a ON a.id_filme=c.id_filme AND a.id_usuario=c.id_usuario " +
                        "WHERE c.id_filme=? ORDER BY c.data_comentario DESC",
                new String[]{String.valueOf(idFilme)}
        );

        List<Comentario> listaComentarios = new ArrayList<>();
        while (cursor.moveToNext()) {
            String nome = cursor.getString(0);
            String comentario = cursor.getString(1);
            float nota = cursor.getFloat(2);
            listaComentarios.add(new Comentario(nome, comentario, nota));
        }

        cursor.close();
        db.close();

        rvComentarios.setLayoutManager(new LinearLayoutManager(this));
        rvComentarios.setAdapter(new ComentarioAdapter(listaComentarios));
    }
}
