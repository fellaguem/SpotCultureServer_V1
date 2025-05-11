package com.spotculture.servlets;

import com.spotculture.utils.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

@WebServlet("/comments")
public class CommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // GET: Liste des commentaires d’un événement
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String eventIdParam = request.getParameter("event_id");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (eventIdParam == null) {
            response.setStatus(400);
            out.println("{\"error\": \"Paramètre 'event_id' manquant\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM comments WHERE event_id = ?")) {

            stmt.setInt(1, Integer.parseInt(eventIdParam));
            ResultSet rs = stmt.executeQuery();

            out.println("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) out.println(",");
                out.print("  {");
                out.print("\"id\": " + rs.getInt("id") + ", ");
                out.print("\"user_id\": " + rs.getInt("user_id") + ", ");
                out.print("\"event_id\": " + rs.getInt("event_id") + ", ");
                out.print("\"content\": \"" + rs.getString("content").replace("\"", "\\\"") + "\", ");
                out.print("\"created_at\": \"" + rs.getTimestamp("created_at") + "\"");
                out.print("}");
                first = false;
            }

            out.println("\n]");
        } catch (SQLException e) {
            response.setStatus(500);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // POST: Ajouter un commentaire
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = request.getParameter("user_id");
        String eventId = request.getParameter("event_id");
        String content = request.getParameter("content");

        if (userId == null || eventId == null || content == null) {
            response.setStatus(400);
            response.getWriter().println("{\"error\": \"Paramètres requis: user_id, event_id, content\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO comments (user_id, event_id, content, created_at) VALUES (?, ?, ?, NOW())")) {

            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(eventId));
            stmt.setString(3, content);

            int rows = stmt.executeUpdate();
            response.getWriter().println("{\"status\": \"Commentaire ajouté (" + rows + " ligne(s))\"}");

        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
