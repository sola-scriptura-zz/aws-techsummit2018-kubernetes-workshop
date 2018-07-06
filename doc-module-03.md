
## Lab 3 : CI/CD for ECS
From this module, we are beginning to develop application using AWS services.
We will complete the following tasks.
- Change database from Mysql to Aurora for Mysql 
- Resize a file and save it to local folder
- Upload a file to S3 using AWS SDK
- Retrieve information from picture using Amazon Rekognition and Translate text using Amazon Translate


### References
Please refer the following information to complete the tasks
[Develop S3](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-s3-objects.html#upload-object)
[Develop Rekognition](https://docs.aws.amazon.com/rekognition/latest/dg/get-started-exercise.html)
[Develop Translate](https://docs.aws.amazon.com/translate/latest/dg/examples-java.html)


## If you start from module-03 (from completed source code)

### 1. Run application and test


	1. Run your application

```
cd module-03

mvn compile package -Dmaven.test.skip=true

java -jar target/module-03-0.1.0.jar

```

	2. Run Unit Test
	
- Run **AWSAWServicesTest** in **hello.logics** of src/test/java

![Unit test](./images/module-03/06.png)


- **You definitely got error above, it is because you don't have Parameter Stores**
- You need to create this following step 4

1. Create a Aurora MySQL instance 
2. Set up MySQL : creating table, user
3. Change parameters in parameter values


	
	
