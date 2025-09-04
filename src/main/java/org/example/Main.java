package org.example;

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
import java.util.List;

public class Main {
    private static final int PORT = 8080;
    private static UsuarioController usuarioController = new UsuarioController();
    private static ProdutoController produtoController = new ProdutoController();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/produtos", new ProdutoHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Servidor rodando em: http://0.0.0.0:" + PORT + "/api/");
    }

    // Cadastro de usuário
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Aqui você precisará ler o InputStream e processar o JSON manualmente
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // TODO: Converter "body" em objeto Usuario manualmente
            Usuario usuario = new Usuario();
            // Exemplo: definir os campos manualmente se o JSON for simples
            // usuario.setNome(...); usuario.setEmail(...);

            boolean ok = usuarioController.criarUsuario(usuario);

            String response = "{\"success\":" + ok + ",\"message\":\"" +
                    (ok ? "Usuário criado com sucesso!" : "Erro ao cadastrar usuário") + "\"}";

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(ok ? 201 : 500, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
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

            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // TODO: Extrair email e senha manualmente do body
            String email = "";
            String senha = "";
            Usuario user = usuarioController.login(email, senha);

            String response;
            if (user != null) {
                response = "{\"success\":true,\"message\":\"Login realizado\"}";
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
                response = "{\"success\":false,\"message\":\"Credenciais inválidas\"}";
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }

    // Produtos
    static class ProdutoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                // TODO: Converter body em Produto manualmente
                Produto produto = new Produto();
                // produto.setNome(...); produto.setPreco(...); ...

                boolean ok = produtoController.criarProduto(produto);
                String response = "{\"success\":" + ok + ",\"message\":\"" +
                        (ok ? "Produto cadastrado!" : "Erro ao cadastrar") + "\"}";

                exchange.sendResponseHeaders(ok ? 201 : 500, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());

            } else if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int usuarioId = Integer.parseInt(query.split("=")[1]);

                List<Produto> lista = produtoController.listarProdutos(usuarioId);

                // TODO: Converter lista de produtos em JSON manualmente
                String response = "[]"; // por enquanto um JSON vazio

                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }
    }
}
