FROM registry.access.redhat.com/ubi8/openjdk-21:1.20

COPY --chown=0:0 cbom-scan-action.jar /cbom-scan-action.jar

CMD ["java","-jar","/cbom-scan-action.jar"]