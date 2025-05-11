package com.spotculture.servlets;

import com.spotculture.utils.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

@WebServlet("/ratings")
public class RatingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // GET : Liste des notes d’un événement
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
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, user_id, rating, created_at FROM ratings WHERE event_id = ?")) {

            stmt.setInt(1, Integer.parseInt(eventIdParam));
            ResultSet rs = stmt.executeQuery();

            out.println("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) out.println(",");
                out.print("  {");
                out.print("\"id\": " + rs.getInt("id") + ", ");
                out.print("\"user_id\": " + rs.getInt("user_id") + ", ");
                out.print("\"rating\": " + rs.getInt("rating") + ", ");
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

    // POST : Ajouter une note
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = request.getParameter("user_id");
        String eventId = request.getParameter("event_id");
        String rating = request.getParameter("rating");

        if (userId == null || eventId == null || rating == null) {
            response.setStatus(400);
            response.getWriter().println("{\"error\": \"Paramètres requis : user_id, event_id, rating\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ratings (user_id, event_id, rating, created_at) VALUES (?, ?, ?, NOW())")) {

            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(eventId));
            stmt.setInt(3, Integer.parseInt(rating));

            int rows = stmt.executeUpdate();
            response.getWriter().println("{\"status\": \"Note ajoutée (" + rows + " ligne(s))\"}");

        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT : Modifier une note existante
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
            sb.append(line);

        String[] params = sb.toString().split("&");
        int id = 0;
        int rating = -1;

        for (String param : params) {
            String[] pair = param.split("=");
            String key = pair[0];
            String value = pair.length > 1 ? pair[1].replace("+", " ") : "";

            switch (key) {
                case "id": id = Integer.parseInt(value); break;
                case "rating": rating = Integer.parseInt(value); break;
            }
        }

        if (id == 0 || rating < 0) {
            response.setStatus(400);
            response.getWriter().println("{\"error\": \"Paramètres requis : id, rating\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET rating = ? WHERE id = ?")) {

            stmt.setInt(1, rating);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            response.getWriter().println("{\"status\": \"Note mise à jour (" + rows + " ligne(s))\"}");

        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE : Supprimer une note
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.setStatus(400);
            response.getWriter().println("{\"error\": \"Paramètre 'id' manquant\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM ratings WHERE id = ?")) {

            stmt.setInt(1, Integer.parseInt(idParam));
            int rows = stmt.executeUpdate();
            response.getWriter().println("{\"status\": \"Note supprimée (" + rows + " ligne(s))\"}");

        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
