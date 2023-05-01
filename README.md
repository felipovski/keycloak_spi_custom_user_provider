# Keycloak SPI - Custom User Provider
Simple Keycloak SPI implementation for accessing an external API

There are two different projects: 
- the first one is an Authentication Mock Service built using Quarkus and Java 17, with endpoints providing data about a user and their authentication.
- the second one is the Custom User Provider, which will request data from the first service.

To build the projects, run maven package commands for each of them (use Java 11 on the second). After that, you can run: 

docker-compose up

With Postgres database, Keycloak and Backend Mock Service up and running, you can now go to custom-user-provider project and input the following command:

docker cp target/custom-user-provider-1.0.jar [container]:opt/bitnami/keycloak/providers/custom.jar

After that, stop keycloak docker service and restart it. On Keycloak console, add a new Realm and Client and you are set to ask for a token:

http://localhost:8080/realms/your_realm/protocol/openid-connect/token

Body (x-www-form-urlencoded) with:

- grant_type: password
- client_id: your_client_id
- client_secret: your_client_secret
- username: username
- password: password

