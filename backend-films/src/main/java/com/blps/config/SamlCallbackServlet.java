package com.blps.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/saml/callback")
public class SamlCallbackServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String samlResponse = request.getParameter("SAMLResponse");
        
        if (samlResponse != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("saml_authenticated", true);
            session.setAttribute("saml_user", "authenticated_user");

            response.sendRedirect("/api/movies");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No SAML Response");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
}
