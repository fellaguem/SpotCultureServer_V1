package com.spotculture.servlets;

import com.spotculture.utils.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

@WebServlet("/import-paris-events")
public class ParisEventImportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // API : limite volontairement fixée à 200 pour maximiser les résultats
    private static final String API_URL = "https://opendata.paris.fr/api/v2/catalog/datasets/que-faire-a-paris-/records?limit=100";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            System.out.println("[Import] Connexion à l'API Paris...");
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject apiResponse = new JSONObject(tokener);

            JSONArray records = apiResponse.getJSONArray("records");

            try (Connection db = DBConnection.getConnection()) {
                int imported = 0;
                int skipped = 0;

                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = records.getJSONObject(i);
                    JSONObject fields = record.getJSONObject("record").getJSONObject("fields");

                    String apiId = fields.optString("id", null);
                    if (apiId == null) {
                        System.out.println("[SKIP] Aucun api_id trouvé.");
                        skipped++;
                        continue;
                    }

                    String dateStart = fields.optString("date_start", null);
                    String dateEnd = fields.optString("date_end", null);
                    if (dateStart == null || dateEnd == null) {
                        System.out.println("[SKIP] Dates manquantes pour " + apiId);
                        skipped++;
                        continue;
                    }

                    // Vérifie si api_id existe déjà
                    PreparedStatement checkStmt = db.prepareStatement("SELECT COUNT(*) FROM events WHERE api_id = ?");
                    checkStmt.setString(1, apiId);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        System.out.println("[SKIP] Événement déjà présent (api_id=" + apiId + ")");
                        skipped++;
                        continue;
                    }

                    // Lecture des autres champs
                    String title = fields.optString("title", "Sans titre");
                    String description = fields.optString("description", "");
                    String location = fields.optString("address_name", "Lieu non précisé");
                    String leadText = fields.optString("lead_text", "");
                    String coverUrl = fields.optString("cover_url", "");

                    PreparedStatement insertStmt = db.prepareStatement(
                            "INSERT INTO events (api_id, title, description, location, date_start, date_end, lead_text, cover_url) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

                    insertStmt.setString(1, apiId);
                    insertStmt.setString(2, title);
                    insertStmt.setString(3, description);
                    insertStmt.setString(4, location);
                    insertStmt.setString(5, dateStart);
                    insertStmt.setString(6, dateEnd);
                    insertStmt.setString(7, leadText);
                    insertStmt.setString(8, coverUrl);

                    insertStmt.executeUpdate();
                    imported++;

                    System.out.println("[OK] Événement importé (api_id=" + apiId + ")");
                }

                out.println("{\"status\": \"Import terminé\", \"imported\": " + imported + ", \"skipped\": " + skipped + "}");
                System.out.println("[Import] Terminé : " + imported + " ajoutés, " + skipped + " ignorés.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.println("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
