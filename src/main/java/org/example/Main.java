package org.example;

import com.google.gson.Gson;
import org.example.Com.DatabaseConnection;
import org.example.Controllers.UsuarioController;
import org.example.Controllers.ProdutoController;
import org.example.Models.Produto;
import org.example.Models.Usuario;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    private static final int PORT = 8080;
    private static UsuarioController usuarioController = new UsuarioController();
    private static ProdutoController produtoController = new ProdutoController();
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/produtos", new ProdutoHandler());

        server.setExecutor(null);
        server.start();

        try(Connection conn = DatabaseConnection.getConnection()){
            System.out.println("Servidor rodando em: http://localhost:" + PORT + "/api/");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Cadastro de usuário
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Usuario usuario = gson.fromJson(body, Usuario.class);
            boolean ok = usuarioController.criarUsuario(usuario);

            String response = gson.toJson(new Response(ok, ok ? "Usuário criado com sucesso!" : "Erro ao cadastrar usuário"));
            byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(ok ? 201 : 500, respBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(respBytes);
            }
        }
    }

    // Login
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Usuario loginUser = gson.fromJson(body, Usuario.class);
            Usuario user = usuarioController.login(loginUser.getEmail(), loginUser.getSenha());

            String response = gson.toJson(new Response(user != null, user != null ? "Login realizado" : "Credenciais inválidas"));
            byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(user != null ? 200 : 401, respBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(respBytes);
            }
        }
    }

    // Produtos
    static class ProdutoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            byte[] respBytes;

            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Produto produto = gson.fromJson(body, Produto.class);
                boolean ok = produtoController.criarProduto(produto);

                String response = gson.toJson(new Response(ok, ok ? "Produto cadastrado!" : "Erro ao cadastrar"));
                respBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(ok ? 201 : 500, respBytes.length);

            } else if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int usuarioId = Integer.parseInt(query.split("=")[1]);

                List<Produto> lista = produtoController.listarProdutos(usuarioId);
                String response = gson.toJson(lista);
                respBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, respBytes.length);

            } else {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(respBytes);
            }
        }
    }

    static class Response {
        private boolean success;
        private String message;

        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
