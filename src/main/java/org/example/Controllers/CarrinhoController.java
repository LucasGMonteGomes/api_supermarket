package org.example.Controllers;

import org.example.Com.DatabaseConnection;
import org.example.Models.CarrinhoItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoController {

    /**
     * Adiciona um item no carrinho
     */
    public boolean adicionarItem(CarrinhoItem item) {
        String sql = "INSERT INTO carrinho_itens (carrinho_id, produto_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getCarrinhoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lista os itens do carrinho de um usuário
     */
    public List<CarrinhoItem> listarItens(int carrinhoId) {
        List<CarrinhoItem> lista = new ArrayList<>();
        String sql = "SELECT * FROM carrinho_itens WHERE carrinho_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carrinhoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CarrinhoItem item = new CarrinhoItem();
                item.setId(rs.getInt("id"));
                item.setCarrinhoId(rs.getInt("carrinho_id"));
                item.setProdutoId(rs.getInt("produto_id"));
                item.setQuantidade(rs.getInt("quantidade"));
                lista.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Remove um item do carrinho
     */
    public boolean removerItem(int itemId) {
        String sql = "DELETE FROM carrinho_itens WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Atualiza a quantidade de um item no carrinho
     */
    public boolean atualizarQuantidade(int itemId, int quantidade) {
        String sql = "UPDATE carrinho_itens SET quantidade = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, itemId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
