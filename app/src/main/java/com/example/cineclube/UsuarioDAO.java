package com.example.cineclube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioDAO {
    private DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Inserir novo usuário
    public boolean inserirUsuario(String nome, String email, String senha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nome", nome);
        valores.put("email", email);
        valores.put("senha", senha);

        long resultado = db.insert("usuarios", null, valores);
        db.close();
        return resultado != -1;
    }

    // Verificar login
    public boolean verificarLogin(String email, String senha) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{email, senha});

        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }
}
