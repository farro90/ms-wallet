FROM openjdk:11
VOLUME /tmp
EXPOSE 8116
ADD ./target/ms-wallet-0.0.1-SNAPSHOT.jar ms-wallet.jar
ENTRYPOINT ["java","-jar","ms-wallet.jar"]