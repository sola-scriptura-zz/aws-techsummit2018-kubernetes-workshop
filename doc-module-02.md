## Lab 2 : Amazon ECS, first Deployment using CLI and CloudFormation

### 1. Create a ECS in your console

#### 1.1 Using a public documentation
This section will explain how to create an ECS cluster using AWS Console.

Please take a look at this documentation, if you want to create a cluster using AWS console :
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create_cluster.html.



#### 1.2 Using a following instruction


### 2. Create a ECS cluster using ECS-CLI

1. Install ecs-cli :
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_CLI_installation.html


Create an ECS cluster with these resources:


1. Create a infrastructure using Cloudformation
- Use **templates/infra.yaml**

```
cd templates

aws cloudformation deploy \
  --stack-name aws-infra-ecscli \
  --template-file infra.yaml \
  --region ap-southeast-1 \
  --capabilities CAPABILITY_IAM
```

2. Run the follow command to capture the output from the CloudFormation template as key/value pairs in the file ecs-cluster.props. 

```
aws cloudformation describe-stacks \
  --region ap-southeast-1 \
  --stack-name aws-infra-ecscli \
  --query 'Stacks[0].Outputs' \
  --output=text | \
  perl -lpe 's/\s+/=/g' | \
  tee ecs-cluster.props
```

3. Setup the environment variables using the file (ecs-cluster.props)

```
set -o allexport
source ecs-cluster.props
set +o allexport
```

4. Configure ECS CLI
```
ecs-cli configure --cluster $ECSCluster --region ap-southeast-1
```