FROM eclipse-temurin:21-jdk

WORKDIR /app

# Thư viện hệ thống cần cho Playwright Chromium trên Linux
RUN apt-get update && apt-get install -y \
    libglib2.0-0 libnss3 libnspr4 libdbus-1-3 libatk1.0-0 \
    libatk-bridge2.0-0 libcups2 libdrm2 libxcb1 libxkbcommon0 \
    libx11-6 libxcomposite1 libxdamage1 libxext6 libxfixes3 \
    libxrandr2 libgbm1 libpango-1.0-0 libcairo2 libasound2t64 \
    --no-install-recommends && rm -rf /var/lib/apt/lists/*

COPY . .

RUN chmod +x gradlew

# Download Gradle dependencies
RUN ./gradlew dependencies --no-daemon -q

# Cài Playwright Chromium tại build time
# → tránh Spring DevTools restart khi Playwright ghi file cache lúc runtime
RUN ./gradlew installPlaywright --no-daemon

EXPOSE 8080

CMD ["./gradlew", "bootRun", "--no-daemon", "--continuous"]
