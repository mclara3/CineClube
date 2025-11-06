package com.example.cineclube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private TextView tvInfo;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioDAO = new UsuarioDAO(this);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etPassword);
        tvInfo = findViewById(R.id.tvInfo);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnIrCadastro = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                tvInfo.setText("Preencha todos os campos!");
                tvInfo.setVisibility(TextView.VISIBLE);
            } else {
                boolean loginValido = usuarioDAO.verificarLogin(email, senha);
                if (loginValido) {
                    tvInfo.setTextColor(getColor(android.R.color.holo_green_dark));
                    tvInfo.setText("Login realizado com sucesso!");
                    tvInfo.setVisibility(TextView.VISIBLE);

                    // Envia email do usuário para o MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_email", email);
                    startActivity(intent);
                    finish();
                } else {
                    tvInfo.setText("E-mail ou senha incorretos!");
                    tvInfo.setVisibility(TextView.VISIBLE);
                }
            }
        });

        btnIrCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, CriarContaActivity.class));
            finish();
        });
    }
}
