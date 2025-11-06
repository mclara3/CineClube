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

import java.util.ArrayList;

public class MinhaContaActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private Button btnEditProfile;
    private RecyclerView rvWatched;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String currentUserEmail;

    private ActivityResultLauncher<Intent> editProfileLauncher;

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

        // ActivityResultLauncher para atualizar dados após edição
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

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MinhaContaActivity.this, EditarContaActivity.class);
            intent.putExtra("user_email", currentUserEmail);
            editProfileLauncher.launch(intent);
        });

        loadUserInfo();
        loadWatchedMovies();

        // Configura bottom nav nesta activity
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Marca item Minha Conta como selecionado
        bottomNav.setSelectedItemId(R.id.nav_conta);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Intent intent = new Intent(MinhaContaActivity.this, MainActivity.class);
                intent.putExtra("user_email", currentUserEmail);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_conta) {
                // Já estamos aqui
                return true;
            }
            return false;
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
        ArrayList<String> filmes = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT f.titulo FROM filmes f " +
                        "INNER JOIN filmes_assistidos fa ON f.id_filme = fa.id_filme " +
                        "INNER JOIN usuarios u ON fa.id_usuario = u.id_usuario " +
                        "WHERE u.email = ?", new String[]{currentUserEmail});

        while (cursor.moveToNext()) {
            filmes.add(cursor.getString(0));
        }
        cursor.close();

        rvWatched.setLayoutManager(new LinearLayoutManager(this));
        rvWatched.setAdapter(new FilmesAdapter(filmes));
    }
}
