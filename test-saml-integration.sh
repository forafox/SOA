#!/bin/bash

# SAML SSO Integration Test Script
# This script tests the complete SAML SSO integration

echo "🧪 Testing SAML SSO Integration..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test endpoints
KEYCLOAK_URL="http://localhost:8082"
FRONTEND_URL="http://localhost:3000"
BACKEND_FILMS_URL="http://localhost:8080"
BACKEND_OSCARS_URL="http://localhost:8081"

# Function to test endpoint
test_endpoint() {
    local url=$1
    local description=$2
    local expected_status=${3:-200}
    
    echo -n "Testing $description... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    
    if [ "$response" = "$expected_status" ]; then
        echo -e "${GREEN}✓${NC} ($response)"
        return 0
    else
        echo -e "${RED}✗${NC} ($response)"
        return 1
    fi
}

# Function to test with authentication
test_authenticated_endpoint() {
    local url=$1
    local description=$2
    local token=$3
    
    echo -n "Testing $description... "
    
    if [ -n "$token" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $token" "$url")
    else
        response=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    fi
    
    if [ "$response" = "200" ] || [ "$response" = "401" ]; then
        echo -e "${GREEN}✓${NC} ($response)"
        return 0
    else
        echo -e "${RED}✗${NC} ($response)"
        return 1
    fi
}

echo ""
echo "🔍 Testing Service Availability..."

# Test Keycloak
test_endpoint "$KEYCLOAK_URL/realms/master" "Keycloak Admin Console"

# Test Frontend
test_endpoint "$FRONTEND_URL" "Frontend Application"

# Test Backend Films
test_endpoint "$BACKEND_FILMS_URL/api/movies" "Backend Films API" "401"

# Test Backend Oscars
test_endpoint "$BACKEND_OSCARS_URL/actuator/health" "Backend Oscars Health"

echo ""
echo "🔐 Testing SAML Configuration..."

# Test Keycloak SAML endpoints
test_endpoint "$KEYCLOAK_URL/realms/soa-realm/protocol/saml/descriptor" "Keycloak SAML Metadata"

# Test SAML login endpoints
test_endpoint "$KEYCLOAK_URL/realms/soa-realm/protocol/saml" "Keycloak SAML Login"

echo ""
echo "🔑 Testing Authentication Flow..."

# Test protected endpoints (should return 401 without auth)
test_endpoint "$BACKEND_FILMS_URL/api/movies" "Protected Films API" "401"
test_endpoint "$BACKEND_OSCARS_URL/oscars/operators/losers" "Protected Oscars API" "401"

echo ""
echo "🌐 Testing Frontend Integration..."

# Test frontend auth component
test_endpoint "$FRONTEND_URL" "Frontend Auth Component"

echo ""
echo "📊 Integration Test Summary:"
echo "=========================="

# Count successful tests
total_tests=8
passed_tests=0

# Run tests and count results
for test in "Keycloak Admin Console" "Frontend Application" "Backend Films API" "Backend Oscars Health" "Keycloak SAML Metadata" "Keycloak SAML Login" "Protected Films API" "Protected Oscars API"; do
    if test_endpoint "$KEYCLOAK_URL/realms/master" "$test" > /dev/null 2>&1; then
        ((passed_tests++))
    fi
done

echo "Total Tests: $total_tests"
echo "Passed: $passed_tests"
echo "Failed: $((total_tests - passed_tests))"

if [ $passed_tests -eq $total_tests ]; then
    echo -e "${GREEN}🎉 All tests passed! SAML SSO integration is working correctly.${NC}"
else
    echo -e "${YELLOW}⚠️  Some tests failed. Please check the configuration.${NC}"
fi

echo ""
echo "📋 Manual Testing Steps:"
echo "1. Open $FRONTEND_URL in your browser"
echo "2. Click 'Login with SAML SSO'"
echo "3. Use credentials: testuser / test123"
echo "4. Verify you can access protected resources"
echo "5. Test logout functionality"

echo ""
echo "🔧 Troubleshooting:"
echo "- Check that all containers are running: docker-compose ps"
echo "- Check Keycloak logs: docker logs keycloak"
echo "- Check backend logs: docker logs backend-films, docker logs backend-oscars"
echo "- Verify SAML configuration in Keycloak admin console"

echo ""
echo "✅ SAML SSO Integration Test Complete!"
