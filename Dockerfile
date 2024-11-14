FROM registry.access.redhat.com/ubi8/openjdk-21:1.20

COPY target/cbom-scan-action*.jar cbom-scan-action.jar

CMD ["java","-jar","cbom-scan-action.jar"]