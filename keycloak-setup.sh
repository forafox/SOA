#!/bin/bash

# Keycloak SAML Setup Script
# This script configures Keycloak with SAML clients for SOA services

KEYCLOAK_URL="http://localhost:8082"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin123!"
REALM_NAME="soa-realm"

echo "🔐 Setting up Keycloak SAML for SOA services..."

# Wait for Keycloak to be ready
echo "⏳ Waiting for Keycloak to be ready..."
until curl -s -f "${KEYCLOAK_URL}/realms/master" > /dev/null; do
    echo "Waiting for Keycloak..."
    sleep 5
done

echo "✅ Keycloak is ready!"

# Get admin token
echo "🔑 Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=${ADMIN_USER}" \
    -d "password=${ADMIN_PASSWORD}" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" | jq -r '.access_token')

if [ "$ADMIN_TOKEN" = "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo "❌ Failed to get admin token"
    exit 1
fi

echo "✅ Admin token obtained"

# Create realm
echo "🏗️ Creating realm: ${REALM_NAME}"
curl -s -X POST "${KEYCLOAK_URL}/admin/realms" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{
        "realm": "'${REALM_NAME}'",
        "enabled": true,
        "displayName": "SOA Services Realm",
        "loginWithEmailAllowed": true,
        "duplicateEmailsAllowed": false,
        "resetPasswordAllowed": true,
        "editUsernameAllowed": false,
        "bruteForceProtected": true
    }'

echo "✅ Realm created"

# Create OAuth2 client for backend-films
echo "🎬 Creating OAuth2 client for backend-films..."
curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{
        "clientId": "backend-films",
        "name": "Backend Films Service",
        "description": "OAuth2 client for backend-films service",
        "enabled": true,
        "protocol": "openid-connect",
        "publicClient": false,
        "serviceAccountsEnabled": true,
        "authorizationServicesEnabled": true,
        "redirectUris": ["http://localhost:8080/*"],
        "webOrigins": ["http://localhost:8080"],
        "baseUrl": "http://localhost:8080",
        "adminUrl": "http://localhost:8080",
        "rootUrl": "http://localhost:8080"
    }'

echo "✅ OAuth2 client for backend-films created"

# Create OAuth2 client for backend-oscars
echo "🏆 Creating OAuth2 client for backend-oscars..."
curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{
        "clientId": "backend-oscars",
        "name": "Backend Oscars Service",
        "description": "OAuth2 client for backend-oscars service",
        "enabled": true,
        "protocol": "openid-connect",
        "publicClient": false,
        "serviceAccountsEnabled": true,
        "authorizationServicesEnabled": true,
        "redirectUris": ["http://localhost:8081/*"],
        "webOrigins": ["http://localhost:8081"],
        "baseUrl": "http://localhost:8081",
        "adminUrl": "http://localhost:8081",
        "rootUrl": "http://localhost:8081"
    }'

echo "✅ OAuth2 client for backend-oscars created"

# Create OAuth2 client for frontend
echo "🌐 Creating OAuth2 client for frontend..."
curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{
        "clientId": "frontend-client",
        "name": "Frontend Application",
        "description": "OAuth2 client for frontend application",
        "enabled": true,
        "protocol": "openid-connect",
        "publicClient": true,
        "redirectUris": ["http://localhost:3000/callback"],
        "webOrigins": ["http://localhost:3000"],
        "baseUrl": "http://localhost:3000",
        "adminUrl": "http://localhost:3000",
        "rootUrl": "http://localhost:3000"
    }'

echo "✅ OAuth2 client for frontend created"

# Create test user
echo "👤 Creating test user..."
curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser",
        "email": "test@example.com",
        "firstName": "Test",
        "lastName": "User",
        "enabled": true,
        "credentials": [{
            "type": "password",
            "value": "test123",
            "temporary": false
        }]
    }'

echo "✅ Test user created"

echo "🎉 Keycloak OAuth2 setup completed!"
echo ""
echo "📋 Configuration Summary:"
echo "  - Realm: ${REALM_NAME}"
echo "  - Admin Console: ${KEYCLOAK_URL}/admin"
echo "  - Test User: testuser / test123"
echo "  - OAuth2 Clients: backend-films, backend-oscars, frontend"
echo ""
echo "🔗 OAuth2 Endpoints:"
echo "  - Authorization: ${KEYCLOAK_URL}/realms/${REALM_NAME}/protocol/openid-connect/auth"
echo "  - Token: ${KEYCLOAK_URL}/realms/${REALM_NAME}/protocol/openid-connect/token"
echo "  - UserInfo: ${KEYCLOAK_URL}/realms/${REALM_NAME}/protocol/openid-connect/userinfo"
echo "  - JWKS: ${KEYCLOAK_URL}/realms/${REALM_NAME}/protocol/openid-connect/certs"
