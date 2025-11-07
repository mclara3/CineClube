package com.example.cineclube;

public class FilmeAvaliado {
    private int idFilme;
    private int idUsuario;
    private String titulo;
    private String dataAvaliacao;
    private float nota;
    private String comentario;

    public FilmeAvaliado(int idFilme, int idUsuario, String titulo, String dataAvaliacao, float nota, String comentario) {
        this.idFilme = idFilme;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.dataAvaliacao = dataAvaliacao;
        this.nota = nota;
        this.comentario = comentario != null ? comentario : "Nenhum comentário";
    }

    public int getIdFilme() { return idFilme; }
    public int getIdUsuario() { return idUsuario; }
    public String getTitulo() { return titulo; }
    public String getDataAvaliacao() { return dataAvaliacao; }
    public float getNota() { return nota; }
    public String getComentario() { return comentario; }
}
