package com.example.cineclube;

public class Comentario {
    private String nomeUsuario;
    private String comentario;
    private float nota;

    public Comentario(String nomeUsuario, String comentario, float nota) {
        this.nomeUsuario = nomeUsuario;
        this.comentario = comentario;
        this.nota = nota;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public float getNota() {
        return nota;
    }
}
