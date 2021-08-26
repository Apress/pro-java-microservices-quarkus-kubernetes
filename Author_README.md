# pro-java-microservices-quarkus-kubernetes-example

To quickly get a running instance of PostgreSQL, you can run it in the official PostgreSQL docker container:
```bash
docker run -d --name demo-postgres \
        -e POSTGRES_USER=developer \
        -e POSTGRES_PASSWORD=p4SSW0rd \
        -e POSTGRES_DB=demo \
        -p 5432:5432 postgres:latest
```
