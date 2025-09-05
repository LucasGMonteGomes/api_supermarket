package org.example.Models;


public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int usuarioId;

    public Produto() {

    }

    public Produto(String nome, double preco, int usuarioId) {
        this.nome = nome;
        this.preco = preco;
        this.usuarioId = usuarioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
}