package com.spotculture.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import com.spotculture.utils.DBConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        String email = req.getParameter("email");
        String pwd   = req.getParameter("password_hash");

        if (email == null || pwd == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("{\"error\":\"Paramètres requis : email, password_hash\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // 1️⃣ Vérifier d'abord que l'utilisateur existe
            try (PreparedStatement checkUser = conn.prepareStatement(
                     "SELECT password_hash FROM users WHERE email = ?")) {
                checkUser.setString(1, email);
                try (ResultSet rs1 = checkUser.executeQuery()) {
                    if (!rs1.next()) {
                        // Pas trouvé → 404
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().println("{\"error\":\"Utilisateur introuvable\"}");
                        return;
                    }
                    String storedHash = rs1.getString("password_hash");
                    // 2️⃣ Vérifier le mot de passe
                    if (!pwd.equals(storedHash)) {
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.getWriter().println("{\"error\":\"Mot de passe incorrect\"}");
                        return;
                    }
                }
            }
            // 3️⃣ Si on arrive ici, l'utilisateur existe ET le mot de passe est OK
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, username, email, created_at FROM users WHERE email = ?")) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Renvoie l'objet JSON de l'utilisateur
                        resp.getWriter().printf(
                          "{\"id\":%d,\"username\":\"%s\",\"email\":\"%s\",\"created_at\":\"%s\"}",
                          rs.getInt("id"),
                          rs.getString("username"),
                          rs.getString("email"),
                          rs.getTimestamp("created_at").toString()
                        );
                    } else {
                        // Cas improbable : existait avant, plus maintenant
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        resp.getWriter().println("{\"error\":\"Erreur interne\"}");
                    }
                }
            }

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String msg = e.getMessage().replace("\"", "\\\"");
            resp.getWriter().println("{\"error\":\"" + msg + "\"}");
        }
    }
}

