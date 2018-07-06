## Preparing a Lab environment : (Time duration : 30 ~ 40 mins)
- This module is for configuring your dev environment
- We will show the two kind of dev environment, one is Eclipse, the other is Cloud9.
- Eclipse IDE is recommended as your dev IDE for studying this workshop



### 2. Using Cloud9 (alternative) 


#### 2.1. Create your Cloud9 environments
Before starting, you need to configure **your EC2 role** for Cloud9 environment instance.

##### 1. launch your instance 

**Use "aws-java-spring-dev-workshop-rehel-dev-1.0"**

![ec2 instance](./images/module-01/01.png)

	2. Select "Red Hat Enterprise Linux 7.5 (HVM), SSD Volume Type" in Singapore Region	
	3. Specify launch setup 
	   Enable public IP
	   Create a new IAM role  -> You need to change a role to have a privilege to access AWS services later
	4. Configure Security Group
![ec2 isnstance](./images/module-01/03.png)	
	
	5. Wait for launching	
	
##### 2. Create a Cloud9 
	1. Create new environemt
	2. Select "Connect and run in remote server (SSH)"
	3. Specify all parameters
![ec2 isnstance](./images/module-01/04.png)	
	
	4. Copy public SSH key 
	5.	 Paste it to ~/.ssh/authorized_keys
	
```
ls -al ~/.ssh/authorized_keys 
-rw-------. 1 ec2-user ec2-user 1157 Apr 24 13:16 /home/ec2-user/.ssh/authorized_keys	

```
	6. Create environment
	7. Open IDE
	
![ec2 isnstance](./images/module-01/05.png)

#### 2.2 Check dev environemt
- Download source codes and run module-01


```
	cd ~/environment
	git clone https://github.com/aws-asean-builders/aws-java-spring-dev-workshop
	cd aws-java-spring-dev-workshop/module-01

	mvn -f pom.xml compile
	mvn -f pom.xml package
	
	### you can skip the test
	mvn -f pom.xml package -Dmaven.test.skip=true

```

<hr>

### 3. Create a Role for EC2 and Lambda
You will need a Role for EC2 and Lambda later for completing this workshop. The creating of 2 kind Role is required.
- A role for EC2 instance
- A role for Lambda

	1. Open IAM
	2. Select Role and click create
![creaet a role](./images/module-00/01.png) 

	3. Select type of trusted entity as EC2 (if you want to create a role for Lambda then select "Lambda" instead of "EC2")

![creaet a role](./images/module-00/02.png) 

	4. Select a Administrator access policy. You can restrict a privelige for this role with selecting a differnt role.
	
![creaet a role](./images/module-00/03.png) 

<hr>

### 4. Detail Configuration ( for reference )
- This instruction only covers Linux (Redhat or CentOS) installation. you need to find a way to set up according to your environment



#### 4.1 Install all SDK and packages

##### 1. Install Java SDK
	1. Check java --version and check the location of Java 
```
$ java -version
java version "1.7.0_171"

$ which java
/usr/bin/java

```
	
	2. Upgrade java version to 1.8 (for development, we need to upgrade Java version and install required packages)
	
```
sudo yum list available java\*      # check available java version
sudo yum -y install java-1.8.0 java-1.8.0-openjdk-devel        # install 1.8 java and javac
sudo yum remove java-1.7.0-openjdk -y # remove 1.7
java -version											# check java version
```

	3. Update JAVA_HOME environment variable in .bashrc

```
vi ~/.bashrc
### add follwing content
export JAVA_HOME=/usr/
```
	
##### 2. Install Maven

```
$ cd /usr/local
$ sudo wget http://www-eu.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz
$ sudo tar xzf apache-maven-3.5.3-bin.tar.gz
$ sudo ln -s apache-maven-3.5.3  maven

$ sudo vi /etc/profile.d/maven.sh

# add following content.
export M2_HOME=/usr/local/maven
export PATH=${M2_HOME}/bin:${PATH}

# load the environment variables in current shell using following command.
source /etc/profile.d/maven.sh

# check the loaded environment variables  
echo $PATH             
```

##### 3. Install nodejs (optional)
https://tecadmin.net/install-latest-nodejs-and-npm-on-centos/

```
sudo yum install -y gcc-c++ make
curl -sL https://rpm.nodesource.com/setup_8.x | sudo -E bash -

# install node and npm
sudo yum install nodejs

# check version
node -v 

```

### 4.2 MySQL installation
- MySQL 5.6 is recommended.

	1. install MySql

refer : https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-centos-7

```
	# msql 5.6
	rpm -ivh http://dev.mysql.com/get/mysql-community-release-el6-5.noarch.rpm
	sudo rpm -ivh mysql-community-release-el6-5.noarch.rpm

	sudo yum install mysql-server

	#start server
	sudo service mysqld start
	
	# grep a temp password
	sudo grep 'temporary password' /var/log/mysqld.log
	
	#set root password
	mysql_secure_installation
	
	# connet as root user with temp password
	mysql -uroot -p
	
	ALTER USER 'root'@'localhost' IDENTIFIED BY 'Work@123!';
	
	# create a database
	create database workshop;
	
```
	
	2. Create user and it's privilege using following SQL commands(use MySQL client in your computer)

```
mysql> CREATE USER 'demouser'@'%' identified by '12345678';; -- Creates the user
mysql> GRANT ALL ON *.* TO 'demouser'@'%' IDENTIFIED BY '12345678';; -- Gives all the privileges to the new user on the newly created 
mysql> FLUSH PRIVILEGES;


```

	4. Create User table 

```
USE workshop;

CREATE TABLE `User` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

```

	5. Check the table created

```
show tables;

describe User;
```


### 5. VNC configuration

refer: 

https://linuxconfig.org/install-gnome-gui-on-rhel-7-linux-server

https://www.itzgeek.com/how-tos/linux/centos-how-tos/configure-vnc-server-on-centos-7-rhel-7.html

#for restirat vnc
remove lock

# rm -f /tmp/.X?-lock
# rm -f /tmp/.X11-unix/X4
