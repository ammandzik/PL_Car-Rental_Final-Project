FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app
ENV JAVA_OPTS="-Xms256m -Xmx512m"
COPY target/Car-Rental-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]