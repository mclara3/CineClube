package com.example.cineclube;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MinhaContaActivity extends BaseActivity {

    private TextView tvUserName, tvUserEmail;
    private Button btnEditProfile, btnLogout;
    private RecyclerView rvWatched;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String currentUserEmail;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhaconta);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        rvWatched = findViewById(R.id.rvWatched);

        // Inicializa a sessão
        session = new SessionManager(this);

        // Recupera email da sessão
        currentUserEmail = session.getUserEmail();
        if (currentUserEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Se vier de outra tela com intent, atualiza o email
        String emailIntent = getIntent().getStringExtra("user_email");
        if (emailIntent != null) {
            currentUserEmail = emailIntent;
        }

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        // Botão Editar Perfil
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MinhaContaActivity.this, EditarContaActivity.class);
            intent.putExtra("user_email", currentUserEmail);
            startActivity(intent);
        });

        // Botão Sair (encerra sessão)
        btnLogout.setOnClickListener(v -> {
            session.clearSession(); // limpa sessão

            Intent intent = new Intent(MinhaContaActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUserInfo();
        loadWatchedMovies();

        setupBottomNav(R.id.nav_conta, currentUserEmail);
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
            if (comentario == null || comentario.isEmpty()) comentario = "Nenhum comentário";

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
        rvWatched.setAdapter(new FilmesAvaliadosAdapter(filmes, db, currentUserEmail, null));
    }
}
