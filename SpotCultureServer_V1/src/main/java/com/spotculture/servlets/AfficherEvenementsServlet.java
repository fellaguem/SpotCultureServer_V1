package com.spotculture.servlets;

import com.spotculture.utils.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/evenements")
public class AfficherEvenementsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<h1>🎭 Liste des événements fsssfffrels :</h1>");
        out.println("<ul>");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT title, date_start, location FROM events")) {

            while (rs.next()) {
                String titre = rs.getString("title");
                String date = rs.getString("date_start");
                String lieu = rs.getString("location");

                out.println("<li><strong>" + titre + "</strong> - " + date + " à " + lieu + "</li>");
            }

        } catch (SQLException e) {
            out.println("<p style='color:red;'>Erreur : " + e.getMessage() + "</p>");
        }

        out.println("</ul>");
    }
}
