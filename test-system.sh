#!/bin/bash

echo "=== Тестирование SOA системы с SAML ===\n"

echo "1. Проверка доступности сервисов:"
echo "   - Keycloak: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/realms/master)"
echo "   - Frontend: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000)"
echo "   - Backend Films: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api/movies)"
echo "   - Backend Oscars: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)"

echo "\n2. Проверка Keycloak realm:"
echo "   - Realm soa-realm: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/realms/soa-realm)"

echo "\n3. Проверка SAML endpoints:"
echo "   - SAML Login: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/realms/soa-realm/protocol/saml)"
echo "   - SAML Metadata: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/realms/soa-realm/protocol/saml/descriptor)"

echo "\n4. Проверка Docker контейнеров:"
docker-compose ps

echo "\n5. Проверка логов (последние 5 строк):"
echo "   - Keycloak:"
docker-compose logs keycloak | tail -5
echo "   - Frontend:"
docker-compose logs frontend | tail -5
echo "   - Backend Films:"
docker-compose logs backend-films | tail -5
echo "   - Backend Oscars:"
docker-compose logs backend-oscars | tail -5

echo "\n=== Тестирование завершено ==="
echo "\nДоступные URL:"
echo "   - Frontend: http://localhost:3000"
echo "   - Keycloak Admin: http://localhost:8082 (admin/admin123)"
echo "   - SAML Login: http://localhost:8082/realms/soa-realm/protocol/saml"
echo "   - Backend Films API: http://localhost:8081/api/movies"
echo "   - Backend Oscars API: http://localhost:8080/oscars/"
echo "\nПользователи для тестирования:"
echo "   - admin/admin123"
echo "   - testuser/test123"
