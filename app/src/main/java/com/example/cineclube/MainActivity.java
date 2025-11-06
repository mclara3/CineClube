package com.example.cineclube;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String currentUserEmail; // email do usuário logado
    private RecyclerView rvMovies;
    private List<Filme> filmes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        currentUserEmail = getIntent().getStringExtra("user_email");

        // Inicializa banco
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        dbHelper.insertInitialMovies(db);

        // RecyclerView
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        // Carrega filmes do banco e seta adapter
        filmes = loadFilmesFromDB();
        FilmesAdapterMain adapter = new FilmesAdapterMain(this, filmes);
        rvMovies.setAdapter(adapter);

        // BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_conta) {
                Intent intent = new Intent(MainActivity.this, MinhaContaActivity.class);
                intent.putExtra("user_email", currentUserEmail);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Ajusta padding edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        // Atualiza lista de filmes ao voltar para a tela
        filmes.clear();
        filmes.addAll(loadFilmesFromDB());
        rvMovies.getAdapter().notifyDataSetChanged();
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
