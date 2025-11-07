package com.example.cineclube;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MinhaContaActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private Button btnEditProfile;
    private RecyclerView rvWatched;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String currentUserEmail;

    private ActivityResultLauncher<Intent> editProfileLauncher;
    private ActivityResultLauncher<Intent> editRatingLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhaconta);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        rvWatched = findViewById(R.id.rvWatched);

        currentUserEmail = getIntent().getStringExtra("user_email");

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        // Launcher para editar perfil
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String updatedEmail = result.getData().getStringExtra("updated_email");
                        currentUserEmail = updatedEmail;
                        loadUserInfo();
                        loadWatchedMovies();
                    }
                }
        );

        // Launcher para editar avaliação
        editRatingLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadWatchedMovies(); // recarrega lista após edição de avaliação
                    }
                }
        );

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MinhaContaActivity.this, EditarContaActivity.class);
            intent.putExtra("user_email", currentUserEmail);
            editProfileLauncher.launch(intent);
        });

        loadUserInfo();
        loadWatchedMovies();

        // Configura bottom nav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_conta);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Intent intent = new Intent(MinhaContaActivity.this, MainActivity.class);
                intent.putExtra("user_email", currentUserEmail);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else return item.getItemId() == R.id.nav_conta;
        });
    }

    private void loadUserInfo() {
        Cursor cursor = db.rawQuery(
                "SELECT nome, email FROM usuarios WHERE email=?",
                new String[]{currentUserEmail});
        if (cursor.moveToFirst()) {
            tvUserName.setText(cursor.getString(0));
            tvUserEmail.setText(cursor.getString(1));
        }
        cursor.close();
    }

    private void loadWatchedMovies() {
        ArrayList<FilmeAvaliado> filmes = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT f.id_filme, u.id_usuario, f.titulo, a.data_avaliacao, a.nota, c.comentario " +
                        "FROM avaliacoes a " +
                        "LEFT JOIN comentarios c ON a.id_usuario = c.id_usuario AND a.id_filme = c.id_filme " +
                        "INNER JOIN filmes f ON a.id_filme = f.id_filme " +
                        "INNER JOIN usuarios u ON a.id_usuario = u.id_usuario " +
                        "WHERE u.email = ? " +
                        "ORDER BY a.data_avaliacao DESC",
                new String[]{currentUserEmail});

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy");

        while (cursor.moveToNext()) {
            int idFilme = cursor.getInt(0);
            int idUsuario = cursor.getInt(1);
            String titulo = cursor.getString(2);
            String dataRaw = cursor.getString(3);
            float nota = cursor.getFloat(4);
            String comentario = cursor.getString(5);
            if (comentario == null || comentario.isEmpty()) {
                comentario = "Nenhum comentário";
            }

            String dataFormatada = dataRaw;
            try {
                Date date = inputFormat.parse(dataRaw);
                dataFormatada = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            filmes.add(new FilmeAvaliado(idFilme, idUsuario, titulo, dataFormatada, nota, comentario));
        }

        cursor.close();

        rvWatched.setLayoutManager(new LinearLayoutManager(this));
        rvWatched.setAdapter(new FilmesAvaliadosAdapter(filmes, db, currentUserEmail, editRatingLauncher));
    }
}
