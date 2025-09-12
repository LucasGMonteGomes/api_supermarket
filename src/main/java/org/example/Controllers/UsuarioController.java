package org.example.Controllers;

import org.example.Com.DatabaseConnection;
import org.example.Models.Usuario;
import org.mindrot.jbcrypt.BCrypt; // import para hash

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioController {

    /**
     * Cadastra um novo usuário no banco de dados
     *
     * @param usuario Objeto Usuario contendo todos os dados
     * @return true se inserido com sucesso, false caso ocorra erro
     */
    public boolean criarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios " +
                "(nome, email, sexo, telefone, endereco, cidade, estado, bairro, pais, senha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            // Criptografa a senha antes de salvar
            String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
            stmt.setString(10, senhaHash);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Realiza login do usuário verificando email e senha
     *
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return Objeto Usuario completo se login válido, null caso inválido
     */
    public Usuario login(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaHash = rs.getString("senha");

                // Verifica senha com BCrypt
                if (BCrypt.checkpw(senha, senhaHash)) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSexo(rs.getString("sexo"));
                    usuario.setTelefone(rs.getString("telefone"));
                    usuario.setEndereco(rs.getString("endereco"));
                    usuario.setCidade(rs.getString("cidade"));
                    usuario.setEstado(rs.getString("estado"));
                    usuario.setBairro(rs.getString("bairro"));
                    usuario.setPais(rs.getString("pais"));
                    return usuario;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean redefinirSenha(String email, String novaSenha) {
        String sql = "UPDATE usuarios SET senha = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Gera o hash da nova senha
            String senhaHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());

            stmt.setString(1, senhaHash);
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
