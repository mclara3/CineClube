package com.example.cineclube;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cineclube.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "senha TEXT NOT NULL)");

        db.execSQL("CREATE TABLE filmes (" +
                "id_filme INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL UNIQUE, " +   // UNIQUE para evitar duplicados
                "descricao TEXT, " +
                "genero TEXT, " +
                "ano INTEGER, " +
                "nota_media REAL DEFAULT 0)");


        db.execSQL("CREATE TABLE avaliacoes (" +
                "id_avaliacao INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "id_filme INTEGER NOT NULL, " +
                "nota REAL CHECK (nota BETWEEN 0 AND 5), " +
                "data_avaliacao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE (id_usuario, id_filme), " +
                "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE, " +
                "FOREIGN KEY (id_filme) REFERENCES filmes(id_filme) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE comentarios (" +
                "id_comentario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "id_filme INTEGER NOT NULL, " +
                "comentario TEXT NOT NULL, " +
                "data_comentario DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE, " +
                "FOREIGN KEY (id_filme) REFERENCES filmes(id_filme) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE filmes_assistidos (" +
                "id_usuario INTEGER NOT NULL, " +
                "id_filme INTEGER NOT NULL, " +
                "data_assistido DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (id_usuario, id_filme), " +
                "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE, " +
                "FOREIGN KEY (id_filme) REFERENCES filmes(id_filme) ON DELETE CASCADE)");

        insertInitialMovies(db);
    }

    public void insertInitialMovies(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO filmes (titulo, descricao, genero, ano) VALUES " +
                "('O Poderoso Chefão', 'A história da família Corleone.', 'Crime/Drama', 1972)," +
                "('Interestelar', 'Viagem espacial para salvar a humanidade.', 'Ficção Científica', 2014)," +
                "('A Origem', 'Um ladrão invade sonhos para roubar segredos.', 'Ficção Científica/Ação', 2010)," +
                "('Clube da Luta', 'A vida de um homem muda ao conhecer o clube da luta.', 'Drama', 1999)," +
                "('Forrest Gump', 'A vida extraordinária de Forrest Gump.', 'Drama/Romance', 1994)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS filmes_assistidos");
        db.execSQL("DROP TABLE IF EXISTS comentarios");
        db.execSQL("DROP TABLE IF EXISTS avaliacoes");
        db.execSQL("DROP TABLE IF EXISTS filmes");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}
