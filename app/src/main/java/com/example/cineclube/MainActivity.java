package com.example.cineclube;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;


public class MainActivity extends BaseActivity {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String currentUserEmail;
    private RecyclerView rvMovies;
    private List<Filme> filmes;
    private int idUsuario;
    private SessionManager session; // sessão

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        session = new SessionManager(this);

        // Pega o email do intent ou da sessão
        currentUserEmail = getIntent().getStringExtra("user_email");
        if (currentUserEmail == null) {
            if (session.isLoggedIn()) {
                currentUserEmail = session.getUserEmail();
            } else {
                // caso não tenha sessão, volta para login
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
        }

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        dbHelper.insertInitialMovies(db);

        // pega id do usuário
        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        if (idUsuario == -1) {
            UsuarioDAO usuarioDAO = new UsuarioDAO(this);
            idUsuario = usuarioDAO.getIdUsuarioPorEmail(currentUserEmail);
        }

        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        filmes = loadFilmesFromDB();
        rvMovies.setAdapter(new FilmesAdapterMain(this, filmes, idUsuario, currentUserEmail));

        setupBottomNav(R.id.nav_home, currentUserEmail);
    }

    private List<Filme> loadFilmesFromDB() {
        List<Filme> lista = new ArrayList<>();
        var cursor = db.rawQuery(
                "SELECT f.id_filme, f.titulo, f.descricao, f.genero, f.ano, " +
                        "COALESCE(AVG(a.nota), 0) AS nota_media " +
                        "FROM filmes f " +
                        "LEFT JOIN avaliacoes a ON f.id_filme = a.id_filme " +
                        "GROUP BY f.id_filme, f.titulo, f.descricao, f.genero, f.ano", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String titulo = cursor.getString(1);
            String descricao = cursor.getString(2);
            String genero = cursor.getString(3);
            int ano = cursor.getInt(4);
            double notaMedia = cursor.getDouble(5);
            lista.add(new Filme(id, titulo, descricao, genero, ano, notaMedia));
        }
        cursor.close();
        return lista;
    }
}
