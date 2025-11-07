package com.example.cineclube;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class EditarContaActivity extends BaseActivity {

    private EditText etNome, etEmail, etSenha;
    private Button btnSalvar, btnCancelar, btnExcluirConta;
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
        btnExcluirConta = findViewById(R.id.btnExcluirConta);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Ativa foreign keys para que ON DELETE CASCADE funcione
        db.execSQL("PRAGMA foreign_keys=ON;");

        currentUserEmail = getIntent().getStringExtra("user_email");

        setupBottomNav(R.id.nav_conta, currentUserEmail);

        loadUserData();

        btnSalvar.setOnClickListener(v -> updateUserData());
        btnCancelar.setOnClickListener(v -> finish());

        // Configura o botão de excluir
        btnExcluirConta.setOnClickListener(v -> confirmDeleteUser());
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
            Intent intent = new Intent();
            intent.putExtra("updated_email", email);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteUser() {
        // Mostra um diálogo de confirmação
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja excluir sua conta? Todos os seus dados serão apagados.")
                .setPositiveButton("Sim", (dialog, which) -> deleteUser())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteUser() {
        int rows = db.delete("usuarios", "email=?", new String[]{currentUserEmail});
        if (rows > 0) {
            Toast.makeText(this, "Conta excluída com sucesso!", Toast.LENGTH_SHORT).show();

            // Redireciona para a tela de login e fecha todas as activities anteriores
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Erro ao excluir conta.", Toast.LENGTH_SHORT).show();
        }
    }

}
