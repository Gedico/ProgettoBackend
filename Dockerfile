FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/*.jar app.jar

# Scriviamo la password direttamente qui (Solo per testare se il Docker si sblocca)
ENV DB_PASSWORD=NuovaPasswordMoltoForte!2026
ENV JWT_SECRET_KEY=unaChiaveSegretaMoltoLungaAlmeno32Caratteri!!!
ENV MAIL_PASSWORD=gwegoetwtuamtxgl
ENV GOOGLE_CLIENT_SECRET=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

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]