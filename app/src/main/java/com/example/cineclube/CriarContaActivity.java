package com.example.cineclube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CriarContaActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etSenha;
    private TextView tvInfo;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criarconta); // o nome do seu XML

        usuarioDAO = new UsuarioDAO(this);

        etNome = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etPassword);
        tvInfo = findViewById(R.id.tvInfo);

        Button btnCriarConta = findViewById(R.id.btnCreateAccount);
        Button btnIrLogin = findViewById(R.id.btnGoToLogin);

        btnCriarConta.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                tvInfo.setText("Preencha todos os campos!");
                tvInfo.setVisibility(TextView.VISIBLE);
            } else {
                boolean sucesso = usuarioDAO.inserirUsuario(nome, email, senha);
                if (sucesso) {
                    tvInfo.setTextColor(getColor(android.R.color.holo_green_dark));
                    tvInfo.setText("Conta criada com sucesso!");
                    tvInfo.setVisibility(TextView.VISIBLE);

                    // Volta para o login
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    tvInfo.setText("Erro ao criar conta (email pode já estar cadastrado)");
                    tvInfo.setVisibility(TextView.VISIBLE);
                }
            }
        });

        btnIrLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
