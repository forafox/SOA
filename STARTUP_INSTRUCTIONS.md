# Инструкция по запуску приложений

## Порты приложений

- **Frontend (Next.js)**: http://localhost:3000
- **Backend Films (Jersey)**: http://localhost:8081/api/
- **Backend Oscars (Spring Boot)**: http://localhost:8080

## Порядок запуска

### 1. Запуск Backend Films (порт 8081)
```bash
cd backend-films
mvn clean compile
mvn exec:java -Dexec.mainClass="com.blps.Main"
```

### 2. Запуск Backend Oscars (порт 8080)
```bash
cd backend-oscars
./gradlew bootRun
```

### 3. Запуск Frontend (порт 3000)
```bash
cd frontend
npm run dev
```

## Исправленные проблемы

1. **Конфликт портов**: Backend Films перенесен с порта 8080 на 8081
2. **CORS настройки**: Добавлены CORS заголовки в оба бэкенда
3. **API клиент**: Обновлен для работы с правильными портами

## Проверка работы

После запуска всех приложений:
1. Откройте http://localhost:3000
2. Переключите "Use Mock Data" в выключенное состояние
3. Попробуйте загрузить список фильмов

API должно работать без CORS ошибок.

## helios

film service
```bash
cd wildfly{version}/bin
./standalone.sh -c standalone-films.xml
```
oscar service
```bash
./standalone.sh -c standalone-oscars.xml
```
Добавить .war в Deployment
film service ports:
```bash
http: 37860
https: 37861
management-http: 38860
```

oscar service ports:
```bash
http: 37863
https: 37862
management-http: 38862
```
Management console film service
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L38860:helios:38860
```

Management console oscar service
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L38862:helios:38862
```

film service api
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L37860:helios:37860
```

oscar service api
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L37863:helios:37863
```

Генерация ключей
```bash
keytool -genkeypair -alias myca -keyalg RSA -keysize 4096 -validity 3650 \
  -storetype PKCS12 -keystore ./pki/ca.p12 -storepass changeit -keypass changeit \
  -dname "CN=My Dev Root CA, O=MyOrg, C=RU" \
  -ext bc=ca:true -ext ku=keyCertSign,cRLSign

```
```bash
keytool -exportcert -rfc -alias myca \
  -keystore ./pki/ca.p12 -storepass changeit \
  -file ./pki/my-root-ca.crt

```
```bash
keytool -genkeypair -alias service-a -keyalg RSA -keysize 2048 -validity 825 \
  -storetype PKCS12 -keystore ./wildfly-37.0.1.Final/standalone/configuration/server.p12 \
  -storepass changeit -keypass changeit \
  -dname "CN=localhost, O=MyOrg, C=RU"
```
```bash
keytool -certreq -alias service-a \
  -keystore ./wildfly-37.0.1.Final/standalone/configuration/server.p12 -storepass changeit \
  -file ./wildfly-37.0.1.Final/standalone/configuration/service-a.csr
```
```bash
keytool -gencert -alias myca \
  -keystore ./pki/ca.p12 -storepass changeit \
  -infile ./wildfly-37.0.1.Final/standalone/configuration/service-a.csr \
  -outfile ./wildfly-37.0.1.Final/standalone/configuration/service-a.crt \
  -rfc -validity 825 \
  -ext san=dns:localhost \
  -ext eku=serverAuth \
  -ext ku=digitalSignature,keyEncipherment
```
```bash
keytool -importcert -noprompt -alias myca \
  -file ./pki/my-root-ca.crt \
  -keystore ./wildfly-37.0.1.Final/standalone/configuration/server.p12 -storepass changeit
```
```bash
keytool -importcert -alias service-a \
  -file ./wildfly-37.0.1.Final/standalone/configuration/service-a.crt \
  -keystore ./wildfly-37.0.1.Final/standalone/configuration/server.p12 -storepass changeit
```
```bash
keytool -importcert -noprompt -alias myca \
  -file ./pki/my-root-ca.crt \
  -keystore ./wildfly-37.0.1.Final/standalone/configuration/truststore.jks -storepass changeit

```
Настройка конфига
```bash
cd wildfly{version}/bin
./jboss-cli.sh --connect --controller=127.0.0.1:{manage http port}
```

```bash
# Elytron: key-store / key-manager / ssl-context
if (outcome != success) of /subsystem=elytron/key-store=serverKS:read-resource
  /subsystem=elytron/key-store=serverKS:add(path="server.p12", type="PKCS12", credential-reference={clear-text="changeit"})
end-if

if (outcome != success) of /subsystem=elytron/key-manager=serverKM:read-resource
  /subsystem=elytron/key-manager=serverKM:add(key-store=serverKS, credential-reference={clear-text="changeit"})
end-if

if (outcome != success) of /subsystem=elytron/server-ssl-context=serverSSC:read-resource
  /subsystem=elytron/server-ssl-context=serverSSC:add(key-manager=serverKM, protocols=["TLSv1.3","TLSv1.2"])
end-if

# HTTPS socket-binding
if (outcome != success) of /socket-binding-group=standard-sockets/socket-binding=https:read-resource
  /socket-binding-group=standard-sockets/socket-binding=https:add(port=8133, interface=public)
else
  /socket-binding-group=standard-sockets/socket-binding=https:write-attribute(name=port, value=8133)
end-if

# Undertow HTTPS listener (и выключаем HTTP)
if (outcome == success) of /subsystem=undertow/server=default-server/http-listener=default:read-resource
  /subsystem=undertow/server=default-server/http-listener=default:remove
end-if


if (outcome != success) of /subsystem=undertow/server=default-server/https-listener=https:read-resource
  /subsystem=undertow/server=default-server/https-listener=https:add(socket-binding=https, ssl-context=serverSSC, verify-client= NOT_REQUESTED, proxy-address-forwarding=true)
else
  /subsystem=undertow/server=default-server/https-listener=https:write-attribute(name=ssl-context, value=serverSSC)
end-if

# Алиасы хоста

try
  /subsystem=undertow/server=default-server/host=default-host:list-add(name=alias,value=service-b)
catch
end-try
try
  /subsystem=undertow/server=default-server/host=default-host:list-add(name=alias,value=localhost)
catch
end-try
try
  /subsystem=undertow/server=default-server/host=default-host:list-add(name=alias,value=127.0.0.1)
catch
end-try

# Remoting через HTTPS + включить http-invoker
if (outcome == success) of /subsystem=remoting/http-connector=http-remoting-connector:read-resource
  /subsystem=remoting/http-connector=http-remoting-connector:write-attribute(name=connector-ref, value=https)
else
  /subsystem=remoting/http-connector=http-remoting-connector:add(connector-ref=https)
end-if

if (outcome == success) of /subsystem=undertow/server=default-server/http-invoker=http-invoker:read-resource
  /subsystem=undertow/server=default-server/http-invoker=http-invoker:write-attribute(name=enabled, value=true)
end-if
```

Если будет warning по-типу не найден server.p12, то скопировать его в /wildfly-37.0.1.Final/bin

выход из cli
```bash
quit
```
