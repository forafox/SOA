#!/bin/bash

echo "=== Тестирование SAML аутентификации ===\n"

echo "1. Проверка SAML endpoints:"
echo "   - SAML Login (общий): $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/realms/soa-realm/protocol/saml)"
echo "   - SAML Login (клиент): $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/realms/soa-realm/protocol/saml/clients/frontend-saml)"
echo "   - SAML Metadata: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/realms/soa-realm/protocol/saml/descriptor)"

echo "\n2. Проверка конфигурации клиента:"
TOKEN=$(curl -s -X POST http://localhost:8082/realms/master/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

CLIENT_ID=$(curl -s http://localhost:8082/admin/realms/soa-realm/clients \
  -H "Authorization: Bearer $TOKEN" | jq -r '.[] | select(.clientId=="frontend-saml") | .id')

echo "   - Client ID: $CLIENT_ID"

curl -s http://localhost:8082/admin/realms/soa-realm/clients/$CLIENT_ID \
  -H "Authorization: Bearer $TOKEN" | jq -r '.protocol, .enabled, .clientId'

echo "\n3. Проверка пользователей:"
curl -s http://localhost:8082/admin/realms/soa-realm/users \
  -H "Authorization: Bearer $TOKEN" | jq -r '.[] | "\(.username): \(.enabled)"'

echo "\n4. Тестирование SAML login URL:"
echo "   Откройте в браузере: http://localhost:8082/realms/soa-realm/protocol/saml/clients/frontend-saml"
echo "   Войдите с учетными данными: admin/admin123"

echo "\n=== Тестирование завершено ==="
