package com.blps.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/saml/logout")
public class SamlLogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Очищаем локальную сессию
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        // Получаем URL для logout в Keycloak
        String keycloakUrl = System.getenv("KEYCLOAK_URL");
        String realm = System.getenv("KEYCLOAK_REALM");
        
        if (keycloakUrl == null) keycloakUrl = "http://localhost:8082";
        if (realm == null) realm = "soa-realm";
        
        String logoutUrl = String.format("%s/realms/%s/protocol/saml/logout", keycloakUrl, realm);
        
        // Перенаправляем на Keycloak для logout
        response.sendRedirect(logoutUrl);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
