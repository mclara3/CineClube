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

    // Adiciona ou atualiza a avaliação e comentário do usuário para um filme
    public boolean adicionarOuAtualizarAvaliacao(int idUsuario, int idFilme, float nota, String comentario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 1️⃣ Atualiza ou insere a nota
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

        // 2️⃣ Atualiza ou insere o comentário
        ContentValues valuesComentario = new ContentValues();
        valuesComentario.put("comentario", comentario);

        // Tenta atualizar primeiro
        int linhasAtualizadas = db.update(
                "comentarios",
                valuesComentario,
                "id_usuario = ? AND id_filme = ?",
                new String[]{String.valueOf(idUsuario), String.valueOf(idFilme)}
        );

        // Se não atualizou nenhuma linha, insere
        if (linhasAtualizadas == 0) {
            valuesComentario.put("id_usuario", idUsuario);
            valuesComentario.put("id_filme", idFilme);
            db.insert("comentarios", null, valuesComentario);
        }

        db.close();
        return resultadoNota != -1;
    }

    // Retorna a nota média do filme (para atualizar a tabela filmes)
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

        // Atualiza a tabela de filmes com a nova média
        ContentValues values = new ContentValues();
        values.put("nota_media", media);
        db.update("filmes", values, "id_filme = ?", new String[]{String.valueOf(idFilme)});

        db.close();
        return media;
    }

    // Retorna a nota que o usuário deu a um filme específico (se existir)
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

    // Retorna o comentário que o usuário deu a um filme (ou null se não houver)
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
