FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Build the application and copy runtime dependencies
RUN mvn -q -DskipTests clean package dependency:copy-dependencies -DoutputDirectory=target/dependency

# Expose port
EXPOSE 8081

# Run the application using the Main class with all dependencies on the classpath
CMD ["sh", "-c", "java -cp 'target/classes:target/dependency/*' com.blps.Main"]
