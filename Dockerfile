FROM registry.access.redhat.com/ubi8/openjdk-21:1.20

COPY cbom-scan-action.jar .
RUN chmod +x cbom-scan-action.jar

CMD ["java","-jar","cbom-scan-action.jar"]