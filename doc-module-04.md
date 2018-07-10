# Lab 4 : Logging and Monitoring

## Log aggregation basic concept
Amazon EC2 Container Service (Amazon ECS) is a highly scalable, high performance container management service that supports Docker containers and allows you to easily run applications on a managed cluster of Amazon EC2 instances.

In this multipart hans-on lab, we have chosen to take a universal struggle amongst IT professionals—log collection—and approach it from different angles to highlight possible architectural patterns that facilitate communication and data sharing between containers.

When building applications on ECS, it is a good practice to follow a micro services approach, which encourages the design of a single application component in a single container. This design improves flexibility and elasticity, while leading to a loosely coupled architecture for resilience and ease of maintenance. However, this architectural style makes it important to consider how your containers will communicate and share data with each other.

### Why is it useful
Application logs are useful for many reasons. They are the primary source of troubleshooting information. In the field of security, they are essential to forensics. Web server logs are often leveraged for analysis (at scale) in order to gain insight into usage, audience, and trends.

Centrally collecting container logs is a common problem that can be solved in a number of ways. The Docker community has offered solutions such as having working containers map a shared volume; having a log-collecting container; and getting logs from a container that logs to stdout/stderr and retrieving them with docker logs.

In this post, we present a solution using Amazon CloudWatch Logs(https://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/WhatIsCloudWatchLogs.html). CloudWatch is a monitoring service for AWS cloud resources and the applications you run on AWS. CloudWatch Logs can be used to collect and monitor your logs for specific phrases, values, or patterns. For example, you could set an alarm on the number of errors that occur in your system logs or view graphs of web request latencies from your application logs. The additional advantages here are that you can look at a single pane of glass for all of your monitoring needs because such metrics as CPU, disk I/O, and network for your container instances are already available on CloudWatch.

### Here is how we are going to do it
Our approach involves setting up a container whose sole purpose is logging. It runs rsyslog(url:http://www.rsyslog.com/) and the CloudWatch Logs agent, and we use Docker Links(url:https://docs.docker.com/userguide/dockerlinks/) to communicate to other containers. With this strategy, it becomes easy to link existing application containers such as Apache and have discrete logs per task. This logging container is defined in each ECS task definition(url:https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_defintions.html), which is a collection of containers running together on the same container instance. With our container log collection strategy, you do not have to modify your Docker image. Any log mechanism tweak is specified in the task definition.

*Note:* This blog provisions a new ECS cluster in order to test the following instructions. Also, please note that we are using the US East (N. Virginia) region throughout this exercise. If you would like to use a different AWS region, please make sure to update your configuration accordingly.

### Linking to a CloudWatch logging container
ll create a container that can be deployed as a syslog host. It will accept standard syslog connections on 514/TCP to rsyslog through container links, and will also forward those logs to CloudWatch Logs via the CloudWatch Logs agent. The idea is that this container can be deployed as the logging component in your architecture (not limited to ECS; it could be used for any centralized logging).

As a proof of concept, we show you how to deploy a container running httpd, clone some static web content (for this example, we clone the ECS documentation), and have the httpd access and error logs sent to the rsyslog service running on the syslog container via container linking. We also send the Docker and ecs-agent logs from the EC2 instance the task is running on. The logs in turn are sent to CloudWatch Logs via the CloudWatch Logs agent.

*Note:* Be sure to replace your information througout the document as necessary (for example: replace "my_docker_hub_repo" with the name of your own Docker Hub repository).

We also assume that all following requirements are in place in your AWS account:

A VPC exists for the account
There is an IAM user with permissions to launch EC2 instances and create IAM policies/roles
SSH keys have been generated
Git and Docker are installed on the image building host
The user owns a Docker Hub account and a repository ("my_docker_hub_repo" in this document)
Let’s get started.

### Create the Docker image
The first step is to create the Docker image to use as a logging container. For this, all you need is a machine that has Git and Docker installed. You could use your own local machine or an EC2 instance.

1. Install Git and Docker. The following steps pertain to the Amazon Linux AMI but you should follow the Git and Docker installation instructions respective to your machine.
<pre><code>$ sudo yum update -y && sudo yum -y install git docker
</code></pre>

2. Make sure that the Docker service is running
<pre><code>$ sudo service docker start
</code></pre>

3. Clone the GitHub repository containing the files you need
<pre><code>$ git clone https://github.com/awslabs/ecs-cloudwatch-logs.git
$ cd ecs-cloudwatch-logs
</code></pre>

You should now have a directory containing two .conf files and a Dockerfile. Feel free to read the content of these files and identify the mechanisms used.

4. Log in to Docker Hub:
<pre><code>$ sudo docker login
</code></pre>

5. Build the container image (replace the my_docker_hub_repo with your repository name):
<pre><code>
$ sudo docker build -t my_docker_hub_repo/cloudwatchlogs .
</code></pre>

6.Push the image to your repo:
<pre><code>$ sudo docker push my_docker_hub_repo/cloudwatchlogs
</code></pre>

Use the build-and-push time to dive deeper into what will live in this container. You can follow along by reading the Dockerfile. Here are a few things worth noting:

 * The third RUN enables remote conncetions for rsyslog.
 * The fourth RUN removes the local6 and local7 facilities to prevent duplicate entries. If you don’t do this, you would see every single apache log entry in /var/log/syslog.
 * The last RUN specifies which output files will receive the log entries on local6 and local7 (e.g., "if the facility is local6 and it is tagged with httpd, put those into this httpd-access.log file").
 * We use Supervisor to run more than one process in this container: rsyslog and the CloudWatch Logs agent.
 * We expose port 514 for rsyslog to collect log entries via the Docker link.

## Create an ECS cluster
Now, create an ECS cluster. One way to do so could be to use the Amazon ECS console first run wizard. For now, though, all you need is an ECS cluster.

7. Navigate to the ECS console and choose Create cluster. Give it a unique name that you have not used before (such as "ECSCloudWatchLogs"), and choose Create.

## Create an IAM role
The next five steps set up a CloudWatch-enabled IAM role with EC2 permissions and spin up a new container instance with this role. All of this can be done manually via the console or you can run a CloudFormation template. To use the CloudFormation template, navigate to CloudFormation console, create a new stack by using this template and go straight to step 14 (just specify the ECS cluster name used above, choose your prefered instance type and select the appropriate EC2 SSH key, and leave the rest unchanged). Otherwise, continue on to step 8.

8. Create an IAM policy for CloudWatch Logs and ECS: point your browser to the IAM console, choose Policies and then Create Policy. Choose Select next to Create Your Own Policy. Give your policy a name (e.g., ECSCloudWatchLogs) and paste the text below as the Policy Document value.

<pre><code>{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:Create*",
        "logs:PutLogEvents"
        ],
      "Effect": "Allow",
      "Resource": "arn:aws:logs:*:*:*"
    },
    {
      "Action": [
        "ecs:CreateCluster",
        "ecs:DeregisterContainerInstance",
        "ecs:DiscoverPollEndpoint",
        "ecs:RegisterContainerInstance",
        "ecs:Submit*",
        "ecs:Poll"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}</code></pre>

9. Create a new IAM EC2 service role and attach the above policy to it. In IAM, choose Roles, Create New Role. Pick a name for the role (e.g., ECSCloudWatchLogs). Choose Role Type, Amazon EC2. Find and pick the policy you just created, click Next Step, and then Create Role.

## Launch an EC2 instance and ECS cluster
10. Launch an instance with the Amazon ECS AMI and the above role in the US East (N. Virginia) region. On the EC2 console page, choose Launch Instance. Choose Community AMIs. In the search box, type "amazon-ecs-optimized" and choose Select for the latest version (2015.03.b). Select the appropriate instance type and choose Next.

11. Choose the appropriate Network value for your ECS cluster. Make sure that Auto-assign Public IP is enabled. Choose the IAM role that you just created (e.g., ECSCloudWatchLogs). Expand Advanced Details and in the User data field, add the following while substituting your_cluster_name for the appropriate name:

<pre><code>#!/bin/bash 
echo ECS_CLUSTER=your_cluster_name >> /etc/ecs/ecs.config 
EOF</code></pre>

12. Choose Next: Add Storage, then Next: Tag Instance. You can give your container instance a name on this page. Choose Next: Configure Security Group. On this page, you should make sure that both SSH and HTTP are open to at least your own IP address.

13. Choose Review and Launch, then Launch and Associate with the appropriate SSH key. Note the instance ID.

14. Ensure that your newly spun-up EC2 instance is part of your container instances (note that it may take up to a minute for the container instance to register with ECS). In the ECS console, select the appropriate cluster. Select the ECS Instances tab. You should see a container instance with the instance ID that you just noted after a minute.

15. On the left pane of the ECS console, choose Task Definitions, then Create new Task Definition. On the JSON tab, paste the code below, overwriting the default text. Make sure to replace "my_docker_hub_repo" with your own Docker Hub repo name and choose Create.

<pre><code>{
  "volumes": [
    {
      "name": "ecs_instance_logs",
      "host": {
        "sourcePath": "/var/log"
      }
    }
  ],
  "containerDefinitions": [
    {
      "environment": [],
      "name": "cloudwatchlogs",
      "image": "my_docker_hub_repo/cloudwatchlogs",
      "cpu": 50,
      "portMappings": [],
      "memory": 64,
      "essential": true,
      "mountPoints": [
        {
          "sourceVolume": "ecs_instance_logs",
          "containerPath": "/mnt/ecs_instance_logs",
          "readOnly": true
        }
      ]
    },
    {
      "environment": [],
      "name": "httpd",
      "links": [
        "cloudwatchlogs"
      ],
      "image": "httpd",
      "cpu": 50,
      "portMappings": [
        {
          "containerPort": 80,
          "hostPort": 80
        }
      ],
      "memory": 128,
      "entryPoint": ["/bin/bash", "-c"],
      "command": [
        "apt-get update && apt-get -y install wget && echo 'CustomLog "| /usr/bin/logger -t httpd -p local6.info -n cloudwatchlogs -P 514" "%v %h %l %u %t %r %>s %b %{Referer}i %{User-agent}i"' >> /usr/local/apache2/conf/httpd.conf && echo 'ErrorLogFormat "%v [%t] [%l] [pid %P] %F: %E: [client %a] %M"' >> /usr/local/apache2/conf/httpd.conf && echo 'ErrorLog "| /usr/bin/logger -t httpd -p local7.info -n cloudwatchlogs -P 514"' >> /usr/local/apache2/conf/httpd.conf && echo ServerName `hostname` >> /usr/local/apache2/conf/httpd.conf && rm -rf /usr/local/apache2/htdocs/* && cd /usr/local/apache2/htdocs && wget -mkEpnp -nH --cut-dirs=4 http://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html && /usr/local/bin/httpd-foreground"
      ],
      "essential": true
    }
  ],
  "family": "cloudwatchlogs"
}</code></pre>

What are some highlights of this task definition?

* The sourcePath value allows the CloudWatch Logs agent running in the log collection container to access the host-based Docker and ECS agent log files. You can change the retention period in CloudWatch Logs.
* The cloudwatchlogs container is marked essential, which means that if log collection goes down, so should the application it is collecting from. Similarly, the web server is marked essential as well. You can easily change this behavior.
* The command section is a bit lengthy. Let us break it down:
We first install wget so that we can later clone the ECS documentation for display on our web server.
  * We then write four lines to httpd.conf. These are the echo commands. They describe how httpd will generate log files and their format. Notice how we tag (-t httpd) these files with httpd and assign them a specific facility (-p localX.info). We also specify that logger is to send these entries to host -n cloudwatchlogs on port -p 514. This will be handled by linking. Hence, port 514 is left untouched on the machine and we could have as many of these logging containers running as we want.
  * %h %l %u %t %r %>s %b %{Referer}i %{User-agent}i should look fairly familiar to anyone who has looked into tweaking Apache logs. The initial %v is the server name and it will be replaced by the container ID. This is how we are able to discern what container the logs come from in CloudWatch Logs.
  * We remove the default httpd landing page with rm -rf.
  * We instead use wget to download a clone of the ECS documentation.
  * And, finally, we start httpd. Note that we redirect httpd log files in our task definition at the command level for the httpd image. Applying the same concept to another image would simply require you to know where your application maintains its log files.

*Note* that we redirect httpd log files in our task definition at the command level for the httpd image. Applying the same concept to another image would simply require you to know where your application maintains its log files.

### Create a service
16. On the services tab in the ECS console, choose Create. Choose the task definition created in step 15, name the service and set the number of tasks to 1. Select Create service.

17. The task will start running shortly. You can press the refresh icon on your service’s Tasks tab. After the status says "Running", choose the task and expand the httpd container. The container instance IP will be a hyperlink under the Network bindings section’s External link. When you select the link you should see a clone of the Amazon ECS documentation. You are viewing this thanks to the httpd container running on your ECS cluster.

18. Open the [CloudWatch Logs console](https://console.aws.amazon.com/cloudwatch/home?region=us-east-1#logs:) to view new ecs entries.

### Conclusion
If you have followed all of these steps, you should now have a two container task running in your ECS cluster. One container serves web pages while the other one collects the log activity from the web container and sends it to CloudWatch Logs. Such a setup can be replicated with any other application. All you need is to specify a different container image and describe the expected log files in the command section.

## Using Amazon CloudWatch Logs Docker driver
You can configure the containers in your tasks to send log information to CloudWatch Logs. If you are using the Fargate launch type for your tasks, this allows you to view the logs from your containers. If you are using the EC2 launch type, this enables you to view different logs from your containers in one convenient location, and it prevents your container logs from taking up disk space on your container instances. This topic helps you get started using the awslogs log driver in your task definitions.

To send system logs from your Amazon ECS container instances to CloudWatch Logs, see [Using CloudWatch Logs with Container Instances](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_cloudwatch_logs.html). For more information about CloudWatch Logs, see [Monitoring Log Files](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/WhatIsCloudWatchLogs.html) in the Amazon CloudWatch User Guide.

**Topics**
* Enabling the awslogs Log Driver for Your Containers
* Creating Your Log Groups
* Available awslogs Log Driver Options
* Specifying a Log Configuration in your Task Definition
* Viewing awslogs Container Logs in CloudWatch Logs

### Enabling the awslogs Log Driver for Your Containers
If you are using the Fargate launch type for your tasks, all you need to do to enable the awslogs log driver is add the required logConfiguration parameters to your task definition. For more information, see [Specifying a Log Configuration in your Task Definition.](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_awslogs.html#specify-log-config)

If you are using the EC2 launch type for your tasks and want to enable the awslogs log driver, your Amazon ECS container instances require at least version 1.9.0 of the container agent. For information about checking your agent version and updating to the latest version, see [Updating the Amazon ECS Container Agent.](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs-agent-update.html)

> **Note**
If you are not using the Amazon ECS-optimized AMI (with at least version 1.9.0-1 of the ecs-init package) for your container instances, you also need to specify that the awslogs logging driver is available on the container instance when you start the agent by using the following environment variable in your docker run statement or environment variable file. For more information, see [Installing the Amazon ECS Container Agent.](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs-agent-install.html) <pre><code>ECS_AVAILABLE_LOGGING_DRIVERS='["json-file","awslogs"]</code></pre>

Your Amazon ECS container instances also require logs:CreateLogStream and logs:PutLogEvents permission on the IAM role with which you launch your container instances. If you created your Amazon ECS container instance role before awslogs log driver support was enabled in Amazon ECS, then you might need to add this permission. If your container instances use the managed IAM policy for container instances, then your container instances should have the correct permissions. For information about checking your Amazon ECS container instance role and attaching the managed IAM policy for container instances, see To check for the ecsInstanceRole in the IAM console.

### Creating Your Log Groups
The awslogs log driver can send log streams to existing log groups in CloudWatch Logs, but it cannot create log groups. Before you launch any tasks that use the awslogs log driver, you should ensure the log groups that you intend your containers to use are created. The console provides an auto-configure option so if you register your task definitions in the console and choose the Auto-configure CloudWatch Logs option your log groups will be created for you. Alternatively, you can manually create your log groups using the following steps.

As an example, you could have a task with a WordPress container (which uses the awslogs-wordpress log group) that is linked to a MySQL container (which uses the awslogs-mysql log group). The sections below show how to create these log groups with the AWS CLI and with the CloudWatch console.

####Creating a Log Group with the AWS CLI
The AWS Command Line Interface (AWS CLI) is a unified tool to manage your AWS services. With just one tool to download and configure, you can control multiple AWS services from the command line and automate them through scripts. For more information, see the AWS Command Line Interface User Guide.

If you have a working installation of the AWS CLI, you can use it to create your log groups. The command below creates a log group called awslogs-wordpress in the us-west-2 region. Run this command for each log group to create, replacing the log group name with your value and region name to the desired log destination.

<pre><code>aws logs create-log-group --log-group-name awslogs-wordpress --region us-west-2
</code></pre>

#### Using the Auto-configuration Feature to Create a Log Group
When registering a task definition in the Amazon ECS console, you have the option to allow Amazon ECS to auto-configure your CloudWatch logs, which will also create the specified log groups for you. The auto-configuration option sets up the CloudWatch logs and log groups with the specified prefix to make it easy.

**To create a log group in the Amazon ECS console**
1. Open the Amazon ECS console at https://console.aws.amazon.com/ecs/.

2. In the left navigation pane, choose Task Definitions, Create new Task Definition.

3. Choose your compatibility option and then Next Step.

4. hoose Add container to begin creating your container definition.

5. In the Storage and Logging section, for Log configuration choose Auto-configure CloudWatch Logs.

6. Enter your awslogs log driver options. For more details, see Specifying a Log Configuration in your Task Definition.

7. Complete the rest of the task definition wizard.

#### Creating a Log Group with the CloudWatch Console
The following procedure creates a log group in the CloudWatch console.

**To create a log group in the CloudWatch console**
1. Open the CloudWatch console at https://console.aws.amazon.com/cloudwatch/.

2. In the left navigation pane, choose Logs.

3. Choose Actions, Create log group.

4. For Log Group Name, enter the name of the log group to create.

5. Choose Create log group to finish.

### Available awslogs Log Driver Options
The awslogs log driver supports the following options in Amazon ECS task definitions. For more information, see [CloudWatch Logs logging driver.](https://docs.docker.com/config/containers/logging/awslogs/)

*awslogs-create-group*

    Required: No

    Specify whether you want the log group automatically created. If this option is not specified, it defaults to false.

        Note
        Your IAM policy must include the logs:CreateLogGroup permission before you attempt to use awslogs-create-group.


*awslogs-datetime-format*

    Required: No

    This option defines a multiline start pattern in Python strftime format. A log message consists of a line that matches the pattern and any following lines that don’t match the pattern. Thus the matched line is the delimiter between log messages.

    One example of a use case for using this format is for parsing output such as a stack dump, which might otherwise be logged in multiple entries. The correct pattern allows it to be captured in a single entry.

    This option always takes precedence if both awslogs-datetime-format and awslogs-multiline-pattern are configured.

        Note

        Multiline logging performs regular expression parsing and matching of all log messages, which may have a negative impact on logging performance.

*awslogs-region*

    Required: Yes

    Specify the region to which the awslogs log driver should send your Docker logs. You can choose to send all of your logs from clusters in different regions to a single region in CloudWatch Logs so that they are all visible in one location, or you can separate them by region for more granularity. Be sure that the specified log group exists in the region that you specify with this option.

*awslogs-group*

    Required: Yes

    You must specify a log group to which the awslogs log driver will send its log streams. For more information, see Creating Your Log Groups.

*awslogs-multiline-pattern*

    Required: No

    This option defines a multiline start pattern using a regular expression. A log message consists of a line that matches the pattern and any following lines that don’t match the pattern. Thus the matched line is the delimiter between log messages.

    This option is ignored if awslogs-datetime-format is also configured.

        Note

        Multiline logging performs regular expression parsing and matching of all log messages. This may have a negative impact on logging performance.

*awslogs-stream-prefix*

    Required: No, unless using the Fargate launch type in which case it is required.

    The awslogs-stream-prefix option allows you to associate a log stream with the specified prefix, the container name, and the ID of the Amazon ECS task to which the container belongs. If you specify a prefix with this option, then the log stream takes the following format:

<pre><code>prefix-name/container-name/ecs-task-id</code></pre>

    If you do not specify a prefix with this option, then the log stream is named after the container ID that is assigned by the Docker daemon on the container instance. Because it is difficult to trace logs back to the container that sent them with just the Docker container ID (which is only available on the container instance), we recommend that you specify a prefix with this option.

    For Amazon ECS services, you could use the service name as the prefix, which would allow you to trace log streams to the service that the container belongs to, the name of the container that sent them, and the ID of the task to which the container belongs.

    You must specify a stream-prefix for your logs in order to have your logs appear in the Log pane when using the Amazon ECS console.

### Specifying a Log Configuration in your Task Definition

Before your containers can send logs to CloudWatch, you must specify the awslogs log driver for containers in your task definition. This section describes the log configuration for a container to use the awslogs log driver. For more information, see [Creating a Task Definition.](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-task-definition.html)

The task definition JSON shown below has a logConfiguration object specified for each container; one for the WordPress container that sends logs to a log group called awslogs-wordpress, and one for a MySQL container that sends logs to a log group called awslogs-mysql. Both containers use the awslogs-example log stream prefix.

<pre><code>{
    "containerDefinitions": [
        {
            "name": "wordpress",
            "links": [
                "mysql"
            ],
            "image": "wordpress",
            "essential": true,
            "portMappings": [
                {
                    "containerPort": 80,
                    "hostPort": 80
                }
            ],
            "logConfiguration": {
                "logDriver": "awslogs",
                "options": {
                    "awslogs-group": "awslogs-wordpress",
                    "awslogs-region": "us-west-2",
                    "awslogs-stream-prefix": "awslogs-example"
                }
            },
            "memory": 500,
            "cpu": 10
        },
        {
            "environment": [
                {
                    "name": "MYSQL_ROOT_PASSWORD",
                    "value": "password"
                }
            ],
            "name": "mysql",
            "image": "mysql",
            "cpu": 10,
            "memory": 500,
            "essential": true,
            "logConfiguration": {
                "logDriver": "awslogs",
                "options": {
                    "awslogs-group": "awslogs-mysql",
                    "awslogs-region": "us-west-2",
                    "awslogs-stream-prefix": "awslogs-example"
                }
            }
        }
    ],
    "family": "awslogs-example"
}</code></pre>

In the Amazon ECS console, the log configuration for the wordpress container is specified as shown in the image below.
![images](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/images/awslogs-console-config.png)

After you have registered a task definition with the awslogs log driver in a container definition log configuration, you can run a task or create a service with that task definition to start sending logs to CloudWatch Logs. For more information, see Running Tasks and Creating a Service.

### Viewing awslogs Container Logs in CloudWatch Logs

After your container instance role has the proper permissions to send logs to CloudWatch Logs, your container agents are updated to at least version 1.9.0, and you have configured and started a task with containers that use the awslogs log driver, your configured containers should be sending their log data to CloudWatch Logs. You can view and search these logs in the console.

** To view your CloudWatch Logs data for a container from the Amazon ECS console **

1. Open the Amazon ECS console at https://console.aws.amazon.com/ecs/.

2. On the Clusters page, select the cluster that contains the task to view.

3. On the Cluster: cluster_name page, choose Tasks and select the task to view.

4. On the Task: task_id page, expand the container view by choosing the arrow to the left of the container name.

5. In the Log Configuration section, choose View logs in CloudWatch, which opens the associated log stream in the CloudWatch console.

![Log configuration](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/images/view_logs_in_cw.png)

** To view your CloudWatch Logs data in the CloudWatch console **

1. Open the CloudWatch console at https://console.aws.amazon.com/cloudwatch/.

2. In the left navigation pane, choose Logs.

3. Select a log group to view. You should see the log groups that you created in Creating Your Log Groups.

![Log Groups](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/images/awslogs-log-groups.png)

4. Choose a log stream to view.

![Log Streams]()