package com.example.cineclube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AvaliacaoDAO {

    private DatabaseHelper dbHelper;

    public AvaliacaoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean adicionarOuAtualizarAvaliacao(int idUsuario, int idFilme, float nota, String comentario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valuesNota = new ContentValues();
        valuesNota.put("id_usuario", idUsuario);
        valuesNota.put("id_filme", idFilme);
        valuesNota.put("nota", nota);

        long resultadoNota = db.insertWithOnConflict(
                "avaliacoes",
                null,
                valuesNota,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        ContentValues valuesComentario = new ContentValues();
        valuesComentario.put("comentario", comentario);

        // Tenta atualizar primeiro
        int linhasAtualizadas = db.update(
                "comentarios",
                valuesComentario,
                "id_usuario = ? AND id_filme = ?",
                new String[]{String.valueOf(idUsuario), String.valueOf(idFilme)}
        );

        if (linhasAtualizadas == 0) {
            valuesComentario.put("id_usuario", idUsuario);
            valuesComentario.put("id_filme", idFilme);
            db.insert("comentarios", null, valuesComentario);
        }

        db.close();
        return resultadoNota != -1;
    }

    public double calcularMediaFilme(int idFilme) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double media = 0.0;

        Cursor cursor = db.rawQuery(
                "SELECT AVG(nota) AS media FROM avaliacoes WHERE id_filme = ?",
                new String[]{String.valueOf(idFilme)}
        );

        if (cursor.moveToFirst()) {
            media = cursor.getDouble(cursor.getColumnIndexOrThrow("media"));
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put("nota_media", media);
        db.update("filmes", values, "id_filme = ?", new String[]{String.valueOf(idFilme)});

        db.close();
        return media;
    }

    public Float buscarNotaUsuario(int idUsuario, int idFilme) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Float nota = null;

        Cursor cursor = db.rawQuery(
                "SELECT nota FROM avaliacoes WHERE id_usuario = ? AND id_filme = ?",
                new String[]{String.valueOf(idUsuario), String.valueOf(idFilme)}
        );

        if (cursor.moveToFirst()) {
            nota = cursor.getFloat(cursor.getColumnIndexOrThrow("nota"));
        }

        cursor.close();
        db.close();
        return nota;
    }

    public String buscarComentarioUsuario(int idUsuario, int idFilme) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String comentario = null;

        Cursor cursor = db.rawQuery(
                "SELECT comentario FROM comentarios WHERE id_usuario = ? AND id_filme = ?",
                new String[]{String.valueOf(idUsuario), String.valueOf(idFilme)}
        );

        if (cursor.moveToFirst()) {
            comentario = cursor.getString(cursor.getColumnIndexOrThrow("comentario"));
        }

        cursor.close();
        db.close();
        return comentario;
    }
}
