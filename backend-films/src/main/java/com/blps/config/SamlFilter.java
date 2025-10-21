package com.blps.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class SamlFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        
        if (session == null || session.getAttribute("saml_authenticated") == null) {
            String keycloakUrl = "http://localhost:8082/realms/soa-realm/protocol/saml";
            String returnUrl = httpRequest.getRequestURL().toString();
            
            httpResponse.sendRedirect(keycloakUrl + "?redirect_uri=" + 
                java.net.URLEncoder.encode(returnUrl, "UTF-8"));
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
