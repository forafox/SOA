#!/bin/bash

# Скрипт для настройки Keycloak realm для SAML

echo "Настройка Keycloak realm для SAML..."

# Ждем, пока Keycloak будет готов
echo "Ожидание готовности Keycloak..."
until curl -s http://localhost:8082/realms/master > /dev/null; do
  echo "Keycloak еще не готов, ждем..."
  sleep 5
done

echo "Keycloak готов!"

# Получаем токен доступа
echo "Получение токена доступа..."
TOKEN=$(curl -s -X POST http://localhost:8082/realms/master/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
  echo "Ошибка получения токена доступа"
  exit 1
fi

echo "Токен получен"

# Создаем realm
echo "Создание realm soa-realm..."
curl -X POST http://localhost:8082/admin/realms \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "realm": "soa-realm",
    "enabled": true,
    "displayName": "SOA Realm",
    "sslRequired": "external",
    "registrationAllowed": true,
    "loginWithEmailAllowed": true,
    "duplicateEmailsAllowed": false,
    "resetPasswordAllowed": true,
    "editUsernameAllowed": false,
    "bruteForceProtected": false,
    "permanentLockout": false,
    "maxFailureWaitSeconds": 900,
    "minimumQuickLoginWaitSeconds": 60,
    "waitIncrementSeconds": 60,
    "quickLoginCheckMilliSeconds": 1000,
    "maxDeltaTimeSeconds": 43200,
    "failureFactor": 30,
    "defaultRoles": ["offline_access", "uma_authorization"],
    "requiredCredentials": ["password"],
    "passwordPolicy": "hashIterations(27500)",
    "otpPolicyType": "totp",
    "otpPolicyAlgorithm": "HmacSHA1",
    "otpPolicyInitialCounter": 0,
    "otpPolicyDigits": 6,
    "otpPolicyLookAheadWindow": 1,
    "otpPolicyPeriod": 30,
    "otpSupportedApplications": ["FreeOTP", "Google Authenticator"],
    "webAuthnPolicyRpEntityName": "keycloak",
    "webAuthnPolicySignatureAlgorithms": ["ES256"],
    "webAuthnPolicyRpId": "",
    "webAuthnPolicyAttestationConveyancePreference": "not specified",
    "webAuthnPolicyAuthenticatorAttachment": "not specified",
    "webAuthnPolicyRequireResidentKey": "not specified",
    "webAuthnPolicyUserVerificationRequirement": "not specified",
    "webAuthnPolicyCreateTimeout": 0,
    "webAuthnPolicyAvoidSameAuthenticatorRegister": false,
    "webAuthnPolicyAcceptableAaguids": [],
    "webAuthnPolicyPasswordlessRpEntityName": "keycloak",
    "webAuthnPolicyPasswordlessSignatureAlgorithms": ["ES256"],
    "webAuthnPolicyPasswordlessRpId": "",
    "webAuthnPolicyPasswordlessAttestationConveyancePreference": "not specified",
    "webAuthnPolicyPasswordlessAuthenticatorAttachment": "not specified",
    "webAuthnPolicyPasswordlessRequireResidentKey": "not specified",
    "webAuthnPolicyPasswordlessUserVerificationRequirement": "not specified",
    "webAuthnPolicyPasswordlessCreateTimeout": 0,
    "webAuthnPolicyPasswordlessAvoidSameAuthenticatorRegister": false,
    "webAuthnPolicyPasswordlessAcceptableAaguids": [],
    "accessTokenLifespan": 300,
    "accessTokenLifespanForImplicitFlow": 900,
    "ssoSessionIdleTimeout": 1800,
    "ssoSessionMaxLifespan": 36000,
    "ssoSessionIdleTimeoutRememberMe": 0,
    "ssoSessionMaxLifespanRememberMe": 0,
    "offlineSessionIdleTimeout": 2592000,
    "offlineSessionMaxLifespanEnabled": false,
    "offlineSessionMaxLifespan": 5184000,
    "clientSessionIdleTimeout": 0,
    "clientSessionMaxLifespan": 0,
    "clientOfflineSessionIdleTimeout": 0,
    "clientOfflineSessionMaxLifespan": 0,
    "accessCodeLifespan": 60,
    "accessCodeLifespanUserAction": 300,
    "accessCodeLifespanLogin": 1800,
    "actionTokenGeneratedByAdminLifespan": 43200,
    "actionTokenGeneratedByUserLifespan": 300,
    "oauth2DeviceCodeLifespan": 600,
    "oauth2DevicePollingInterval": 5,
    "internationalizationEnabled": false,
    "supportedLocales": [],
    "defaultLocale": "",
    "browserFlow": "browser",
    "registrationFlow": "registration",
    "directGrantFlow": "direct grant",
    "resetCredentialsFlow": "reset credentials",
    "clientAuthenticationFlow": "clients",
    "dockerAuthenticationFlow": "docker auth",
    "attributes": {
      "cibaBackchannelTokenDeliveryMode": "poll",
      "cibaExpiresIn": "120",
      "cibaInterval": "5",
      "cibaAuthRequestedUserHint": "login_hint",
      "parRequestUriLifespan": "60",
      "frontendUrl": "",
      "adminEventsEnabled": "false",
      "adminEventsDetailsEnabled": "false",
      "eventsEnabled": "false",
      "eventsExpiration": "0",
      "eventsListeners": "jboss-logging",
      "enabledEventTypes": [],
      "eventListeners": "jboss-logging",
      "userManagedAccessAllowed": "false",
      "clientPolicies": "{\"policies\":[]}",
      "clientProfiles": "{\"profiles\":[]}",
      "oauth2DeviceCodeLifespan": "600",
      "oauth2DevicePollingInterval": "5"
    },
    "userManagedAccessAllowed": false,
    "clientProfiles": {
      "profiles": []
    },
    "clientPolicies": {
      "policies": []
    }
  }'

echo "Realm создан"

# Создаем роли
echo "Создание ролей..."
curl -X POST http://localhost:8082/admin/realms/soa-realm/roles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "admin", "description": "Administrator role"}'

curl -X POST http://localhost:8082/admin/realms/soa-realm/roles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "user", "description": "Regular user role"}'

echo "Роли созданы"

# Создаем пользователей
echo "Создание пользователей..."

# Создаем admin пользователя
curl -X POST http://localhost:8082/admin/realms/soa-realm/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "enabled": true,
    "emailVerified": true,
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@soa.local",
    "credentials": [{
      "type": "password",
      "value": "admin123",
      "temporary": false
    }]
  }'

# Создаем test пользователя
curl -X POST http://localhost:8082/admin/realms/soa-realm/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "enabled": true,
    "emailVerified": true,
    "firstName": "Test",
    "lastName": "User",
    "email": "test@soa.local",
    "credentials": [{
      "type": "password",
      "value": "test123",
      "temporary": false
    }]
  }'

echo "Пользователи созданы"

# Создаем SAML клиентов
echo "Создание SAML клиентов..."

# Frontend SAML клиент
curl -X POST http://localhost:8082/admin/realms/soa-realm/clients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "frontend-saml",
    "name": "Frontend SAML Client",
    "description": "React Frontend Application with SAML",
    "enabled": true,
    "protocol": "saml",
    "clientAuthenticatorType": "client-secret",
    "secret": "frontend-saml-secret",
    "redirectUris": ["http://localhost:3000/*"],
    "webOrigins": ["http://localhost:3000"],
    "attributes": {
      "saml.assertion.signature": "false",
      "saml.force.post.binding": "false",
      "saml.multivalued.roles": "false",
      "saml.encrypt": "false",
      "saml.server.signature": "false",
      "saml.server.signature.keyinfo.ext": "false",
      "saml_force_name_id_format": "false",
      "saml.client.signature": "false",
      "saml.authnstatement": "false",
      "saml.onetimeuse.condition": "false"
    }
  }'

# Backend Films SAML клиент
curl -X POST http://localhost:8082/admin/realms/soa-realm/clients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "backend-films-saml",
    "name": "Backend Films SAML Client",
    "description": "JAX-RS Backend Films Service with SAML",
    "enabled": true,
    "protocol": "saml",
    "clientAuthenticatorType": "client-secret",
    "secret": "backend-films-saml-secret",
    "redirectUris": ["http://localhost:8081/*"],
    "webOrigins": ["http://localhost:8081"],
    "attributes": {
      "saml.assertion.signature": "false",
      "saml.force.post.binding": "false",
      "saml.multivalued.roles": "false",
      "saml.encrypt": "false",
      "saml.server.signature": "false",
      "saml.server.signature.keyinfo.ext": "false",
      "saml_force_name_id_format": "false",
      "saml.client.signature": "false",
      "saml.authnstatement": "false",
      "saml.onetimeuse.condition": "false"
    }
  }'

# Backend Oscars SAML клиент
curl -X POST http://localhost:8082/admin/realms/soa-realm/clients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "backend-oscars-saml",
    "name": "Backend Oscars SAML Client",
    "description": "Spring Boot Backend Oscars Service with SAML",
    "enabled": true,
    "protocol": "saml",
    "clientAuthenticatorType": "client-secret",
    "secret": "backend-oscars-saml-secret",
    "redirectUris": ["http://localhost:8080/*"],
    "webOrigins": ["http://localhost:8080"],
    "attributes": {
      "saml.assertion.signature": "false",
      "saml.force.post.binding": "false",
      "saml.multivalued.roles": "false",
      "saml.encrypt": "false",
      "saml.server.signature": "false",
      "saml.server.signature.keyinfo.ext": "false",
      "saml_force_name_id_format": "false",
      "saml.client.signature": "false",
      "saml.authnstatement": "false",
      "saml.onetimeuse.condition": "false"
    }
  }'

echo "SAML клиенты созданы"

echo "Настройка Keycloak завершена!"
echo "Realm: soa-realm"
echo "Пользователи: admin/admin123, testuser/test123"
echo "SAML клиенты: frontend-saml, backend-films-saml, backend-oscars-saml"
echo "Keycloak Admin Console: http://localhost:8082"
echo "Frontend: http://localhost:3000"
