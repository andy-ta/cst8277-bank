# Generating a certificate
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 400

# Docker
## Maven
`mvnw clean package -Pprod`
## Build
`docker build -t cst8277bank .`
## Run
`docker run -d -p 443:8443 -p 80:8080 -t cst8277bank`
## Postgres
`docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d -p 5432:5432 postgres`
## Docker-Compose
`docker-compose up`
