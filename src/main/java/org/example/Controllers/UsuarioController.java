package org.example.Controllers;

import org.example.Com.DatabaseConnection;
import org.example.Models.Usuario;
import java.sql.*;

public class UsuarioController {
    public boolean criarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, sexo, telefone, endereco, cidade, estado, bairro, pais, senha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSexo());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getEndereco());
            stmt.setString(6, usuario.getCidade());
            stmt.setString(7, usuario.getEstado());
            stmt.setString(8, usuario.getBairro());
            stmt.setString(9, usuario.getPais());
            stmt.setString(10, usuario.getSenha());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario login(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                return usuario;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

