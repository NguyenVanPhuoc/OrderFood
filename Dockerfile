FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy toàn bộ project (sẽ bị override bởi volume mount)
COPY . .

# Cấp quyền thực thi cho gradlew
RUN chmod +x gradlew

# Chỉ download dependencies, KHÔNG build
RUN ./gradlew dependencies --no-daemon

EXPOSE 8080

# Chạy bootRun với no-daemon để dễ debug
CMD ["./gradlew", "bootRun", "--no-daemon", "--continuous"]