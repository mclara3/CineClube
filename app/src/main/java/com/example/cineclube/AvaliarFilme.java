package com.example.cineclube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AvaliarFilme extends AppCompatActivity {

    private TextView tvRateMovieTitle;
    private RatingBar ratingBarMovie;
    private MaterialButton btnSubmitMovieRating, btnCancelRating;
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNav;

    private int idFilme;
    private String tituloFilme;

    private int idUsuario;
    private AvaliacaoDAO avaliacaoDAO;

    private EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avaliar_filme);

        // Inicializa DAO e views
        avaliacaoDAO = new AvaliacaoDAO(this);

        tvRateMovieTitle = findViewById(R.id.tvRateMovieTitle);
        ratingBarMovie = findViewById(R.id.ratingBarMovie);
        btnSubmitMovieRating = findViewById(R.id.btnSubmitMovieRating);
        btnCancelRating = findViewById(R.id.btnCancelRating);
        topAppBar = findViewById(R.id.topAppBar);
        bottomNav = findViewById(R.id.bottomNav);
        etComment = findViewById(R.id.etComment);

        // Recebe dados do Intent
        Intent intent = getIntent();
        idFilme = intent.getIntExtra("id_filme", -1);
        tituloFilme = intent.getStringExtra("filme_titulo");
        idUsuario = intent.getIntExtra("id_usuario", -1);

        // Nota e comentário existentes (para edição)
        float notaExistente = intent.getFloatExtra("nota", 0f);
        String comentarioExistente = intent.getStringExtra("comentario");

        if (tituloFilme != null)
            tvRateMovieTitle.setText(tituloFilme);

        ratingBarMovie.setRating(notaExistente);
        etComment.setText(comentarioExistente != null ? comentarioExistente : "");

        // Botão Enviar avaliação
        btnSubmitMovieRating.setOnClickListener(v -> {
            float nota = ratingBarMovie.getRating();
            String comentario = etComment.getText().toString().trim();

            if (nota == 0) {
                Toast.makeText(this, "Por favor, selecione uma nota.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sucesso = avaliacaoDAO.adicionarOuAtualizarAvaliacao(idUsuario, idFilme, nota, comentario);

            if (sucesso) {
                Toast.makeText(this, "Avaliação registrada com sucesso!", Toast.LENGTH_SHORT).show();

                // Cria Intent de resultado
                Intent resultado = new Intent();
                resultado.putExtra("id_filme", idFilme);
                resultado.putExtra("id_usuario", idUsuario);
                resultado.putExtra("nota", nota);
                resultado.putExtra("comentario", comentario);

                setResult(RESULT_OK, resultado); // envia resultado para quem chamou
                finish(); // fecha esta Activity
            }
            else {
                Toast.makeText(this, "Erro ao salvar avaliação.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botão Cancelar → volta para MainActivity
        btnCancelRating.setOnClickListener(v -> {
            Intent voltarIntent = new Intent(AvaliarFilme.this, MainActivity.class);
            startActivity(voltarIntent);
            finish();
        });

        // Botão de navegação da AppBar
        topAppBar.setNavigationOnClickListener(v -> finish());
    }
}
