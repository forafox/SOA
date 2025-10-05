FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /workspace


COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY src ./src
RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:17-jdk

# Версию можно переопределить при сборке:
ARG WF_VER=34.0.1.Final
ENV WF_VER=${WF_VER}

WORKDIR /opt


RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL -o wildfly.zip "https://github.com/wildfly/wildfly/releases/download/${WF_VER}/wildfly-${WF_VER}.zip" \
    && unzip -q wildfly.zip && rm wildfly.zip
ENV JBOSS_HOME=/opt/wildfly-${WF_VER}


COPY --from=builder /workspace/target/*.war ${JBOSS_HOME}/standalone/deployments/ROOT.war


EXPOSE 8080 8443 9990

# Настраиваем окружение
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV MGMT_USER=admin
ENV MGMT_PASS=admin123!


RUN ${JBOSS_HOME}/bin/add-user.sh --user ${MGMT_USER} --password ${MGMT_PASS} --silent


COPY certs/server.p12 /opt/server.p12
COPY certs/truststore.jks /opt/truststore.jks
ENV JAVA_OPTS="${JAVA_OPTS} -Djavax.net.ssl.trustStore=/opt/truststore.jks -Djavax.net.ssl.trustStorePassword=changeit"


CMD ["sh", "-c", "${JBOSS_HOME}/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0"]