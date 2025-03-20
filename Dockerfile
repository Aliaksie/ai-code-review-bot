# Step 1: Use an official OpenJDK image as the base image
FROM openjdk:24-jdk-slim AS build

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3.1: Copy the Maven wrapper and its directory to the container
COPY .mvnw .mvnw
COPY .mvnw/wrapper .mvnw/wrapper

# Step 3.2: Copy the Maven wrapper and pom.xml
COPY mvnw pom.xml ./

# Step 4: Install dependencies (uses the Maven wrapper)
RUN ./mvnw dependency:go-offline

# Step 5: Copy the rest of the application source code
COPY src /app/src

# Step 6: Build the application
RUN ./mvnw clean package -DskipTests

# Step 7: Use a minimal...
FROM openjdk:24-jdk-slim

# Step 8: Set the working directory in the final container
WORKDIR /app

# Step 9: Copy the jar file built in the previous step
COPY --from=build /app/target/ai-code-review-bot-1.0.0.jar /app/ai-code-review-bot.jar

# Step 10: Expose the port (Spring Boot default port)
EXPOSE 8080

# Step 11: Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/ai-code-review-bot.jar"]
