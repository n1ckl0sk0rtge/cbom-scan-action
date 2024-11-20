FROM openjdk:21

COPY --chown=0:0 cbom-scan-action.jar /cbom-scan-action.jar

CMD ["java","-jar","/cbom-scan-action.jar"]