package com.example.cineclube;

public class Filme {
    private int id;
    private String titulo;
    private String descricao;
    private String genero;
    private int ano;
    private double notaMedia;

    public Filme(int id, String titulo, String descricao, String genero, int ano, double notaMedia) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.genero = genero;
        this.ano = ano;
        this.notaMedia = notaMedia;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getGenero() { return genero; }
    public int getAno() { return ano; }
    public double getNotaMedia() { return notaMedia; }
}
