## 1. Lab 1 :  Create Your First Docker Images
In this module, we introduce the fundamental concept of CodeStar and how to build a quick CI/CD pipeline with CodeStar. You will be provided with hands-on on migrating your project to CodeStar project you created and how to build docker environment for your application
- Create a docker for your application
- Create a CodeBuilder for Java compilation and Dockerization
- Push docker image to ECR
- Getting a docker from ECR and check it's availability on local machine

### 1. Create a docker for this application.


#### 1.1 Create your first local docker 

refer : 
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html#docker-basics-create-image

- You need to install docker in your local server

```
sudo yum install -y docker
```

##### Run your first docker application

1. Check a Dockerfile

```
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```
2. Create a docker image
	
```	
docker build -t hello-world . --build-arg JAR_FILE="./target/<YOUR_ARTIFACT_FILE>"
```

3. Run docker in your local machine
	
```
docker run -p 8080:8080 -it hello-world bash
```

4. Check the application in your console
	
5. Change a host port for 80
	
```
docker run -p 80:8080 -it hello-world bash
```

##### More commands for docker

1. run docker as a daemon

```
docker run -d -p 80:8080 --name=test-1 hello-world 
	
docker build -t hello-world . --build-arg JAR_FILE="<YOUR_ARTIFACT_FILE>"
	
```
2. Check running docker and stop it

```
docker ps

docker stop <CONTAINER ID>
```

3. Remove all container

```
#stop all running docker
	docker stop $(docker ps -a -q)
# Delete all containers
docker rm $(docker ps -a -q)
# Delete all images
docker rmi $(docker images -q)
```

##### Debug


1. List stack:

```
docker stack ls
```

2. List services in the stack:

```
docker stack services myapp
```

3. List containers:

```
docker container ls -f name=myapp*
```

4. Get logs for all the containers in the webapp service:

```
docker service logs myapp_webapp-service
```


#### 1.2 Create a ECR repository

1. Run a following AWS CLI command

```
aws ecr create-repository --repository-name java-workshop	
	
```
2. Check response and save a repository ARN

```
{
    "repository": {
        "registryId": "550622896891", 
        "repositoryName": "java-workshop	", 
        "repositoryArn": "arn:aws:ecr:ap-southeast-1:<account id>:repository/java-workshop	", 
        "createdAt": 1516947869.0, 
        "repositoryUri": "<account id>.dkr.ecr.ap-southeast-1.amazonaws.com/java-workshop	"
    }
}

```

#### 1.3 Push your images

```
docker build -t java-workshop:latest. --build-arg JAR_FILE="module-09.jar"
docker tag java-workshop:latest  <aws_account_id>.dkr.ecr.<your_region>.amazonaws.com/java-workshop:latest 

docker push  <aws_account_id>.dkr.ecr.<your_region>.amazonaws.com/java-workshop:latest
```


#### 1.4 Check pushed image in your local machine

1. You can describe the images in a repository using following command.

```
aws ecr describe-images --repository-name java-workshop

```

2. Pull the image using the docker pull

```
docker pull <aws_account_id>.dkr.ecr.<your_region>.amazonaws.com/java-workshop:latest

docker pull <aws_account_id>.dkr.ecr.ap-southeast-1.amazonaws.com/java-workshop:latest

docker images 

docker run -d -p 80:8080 --name=hello-world <IMAGE_ID>

docker run -d -p 80:8080 --name=hello-world 6f9c0d0b1c56

docker ps
```


