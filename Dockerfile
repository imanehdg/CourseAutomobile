FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY src/ src/
RUN mkdir out && javac -d out src/*.java
CMD ["java", "-cp", "out", "Menu", "--auto"]