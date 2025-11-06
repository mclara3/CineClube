package com.example.cineclube;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditarContaActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etSenha;
    private Button btnSalvar, btnCancelar;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarconta);

        etNome = findViewById(R.id.etNomeUsuario);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        currentUserEmail = getIntent().getStringExtra("user_email");

        loadUserData();

        btnSalvar.setOnClickListener(v -> updateUserData());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        var cursor = db.rawQuery("SELECT nome, email, senha FROM usuarios WHERE email=?", new String[]{currentUserEmail});
        if(cursor.moveToFirst()){
            etNome.setText(cursor.getString(0));
            etEmail.setText(cursor.getString(1));
            etSenha.setText(cursor.getString(2));
        }
        cursor.close();
    }

    private void updateUserData() {
        String nome = etNome.getText().toString();
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        ContentValues cv = new ContentValues();
        cv.put("nome", nome);
        cv.put("email", email);
        cv.put("senha", senha);

        int rows = db.update("usuarios", cv, "email=?", new String[]{currentUserEmail});
        if(rows > 0){
            Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show();

            // Retorna email atualizado para a activity anterior
            Intent intent = new Intent();
            intent.putExtra("updated_email", email);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
        }
    }
}
