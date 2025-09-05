package org.example.Models;

public class CarrinhoItem {
    private int id;
    private int carrinhoId; // ID do carrinho do usuário
    private int produtoId;
    private int quantidade;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCarrinhoId() { return carrinhoId; }
    public void setCarrinhoId(int carrinhoId) { this.carrinhoId = carrinhoId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
