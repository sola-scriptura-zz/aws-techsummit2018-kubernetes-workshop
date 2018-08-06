= Technical fundamental hardening (Container on AWS)
:toc:

== Lab1 개발 단계에서 어플리케이션을 설계하고 도커라이징 할 때 고려해 할 사항

== Lab2 클라우드 네이티브 아키텍처를 위한 Docker File 구조와 서비스, 작업 정의 연관 관계

== 개발 장비에서 Dockerizing 하기

This repo contains a simple application that consists of three microservices. The sample application uses three services:

. `webapp`: Web application microservice calls `greeting` and `name` microservice to generate a greeting for a person.
. `greeting`: A microservice that returns a greeting.
. `name`: A microservice that returns a person’s name based upon `{id}` in the URL.

Each application is deployed using different AWS Compute options.

== Build and Test Services using Maven

. Each microservice is in a different repo:
+
[cols="1,3"]
|====
| `greeting` | https://github.com/arun-gupta/microservices-greeting
| `name` | https://github.com/arun-gupta/microservices-name
| `webapp` | https://github.com/arun-gupta/microservices-webapp
|====
+
. Clone all the repos. Open each one in a separate terminal.
. Run `greeting` service: `mvn wildfly-swarm:run`
.. Optionally test: `curl http://localhost:8081/resources/greeting`
. Run `name` service: `mvn wildfly-swarm:run`
.. Optionally test:
... `curl http://localhost:8082/resources/names`
... `curl http://localhost:8082/resources/names/1`
. Run `webapp` service: `mvn wildfly-swarm:run`
. Run the application: `curl http://localhost:8080/`

== Docker

=== Create Docker Images

`mvn package -Pdocker` for each repo will create the Docker image.

By default, the Docker image name is `arungupta/<service>` where `<service>` is `greeting`, `name` or `webapp`. The image can be created in your repo:

  mvn package -Pdocker -Ddocker.repo=<repo>

By default, the `latest` tag is used for the image. A different tag may be specified as:

  mvn package -Pdocker -Ddocker.tag=<tag>

=== Push Docker Images to Registry

Push Docker images to the registry:

  mvn install -Pdocker

=== Deployment to Docker Swarm

. `docker swarm init`
. `cd apps/docker`
. `docker stack deploy --compose-file docker-compose.yaml myapp`
. Access the application: `curl http://localhost:8080`
.. Optionally test the endpoints:
... Greeting endpoint: `curl http://localhost:8081/resources/greeting`
... Name endpoint: `curl http://localhost:8082/resources/names/1`
. Remove the stack: `docker stack rm myapp`

==== Debug

. List stack:

  docker stack ls

. List services in the stack:

  docker stack services myapp

. List containers:

  docker container ls -f name=myapp*

. Get logs for all the containers in the `webapp` service:

  docker service logs myapp_webapp-service