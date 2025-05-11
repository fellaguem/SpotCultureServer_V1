package com.spotculture.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Type de réponse : du HTML
        response.setContentType("text/html");

        // Écriture de la réponse HTML
        PrintWriter out = response.getWriter();
        out.println("<h1>Hello depuis xxxxxxxx !</h1>");
    }
}
