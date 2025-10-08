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


    /**
     * Faz login via Google. Se o usuário não existir, cria no banco com senha aleatória.
     *
     * @param nome  Nome do usuário vindo do Google
     * @param email Email do usuário vindo do Google
     * @return Usuario existente ou recém-criado, null se houver erro
     */

    public Usuario loginGoogle(String nome, String email) {
        String sqlBusca = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca)) {

            stmtBusca.setString(1, email);
            ResultSet rs = stmtBusca.executeQuery();

            if (rs.next()) {
                // Usuário existe → retorna objeto
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
            } else {
                // Usuário não existe → cria com senha aleatória
                String senhaAleatoria = java.util.UUID.randomUUID().toString(); // senha temporária
                String sqlInsere = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";

                try (PreparedStatement stmtInsere = conn.prepareStatement(sqlInsere, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmtInsere.setString(1, nome);
                    stmtInsere.setString(2, email);
                    stmtInsere.setString(3, BCrypt.hashpw(senhaAleatoria, BCrypt.gensalt()));

                    int affectedRows = stmtInsere.executeUpdate();
                    if (affectedRows == 0) return null;

                    ResultSet generatedKeys = stmtInsere.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        Usuario novoUsuario = new Usuario();
                        novoUsuario.setId(generatedKeys.getInt(1));
                        novoUsuario.setNome(nome);
                        novoUsuario.setEmail(email);
                        return novoUsuario;
                    }
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