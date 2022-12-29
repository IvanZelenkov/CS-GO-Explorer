# Database Bot Manager

![DBMSolutionArchitecture](https://user-images.githubusercontent.com/64171964/208028293-644c8275-3cc7-464c-9b5d-fa73e5d95e6b.png)

**NEW UPDATE: A web interface has been created, so the application is currently available online at the following link: https://dev9055.dqufhvl8gk5wp.amplifyapp.com**

## DBMLauncher

### Content
- DBMLauncher
   - src
      - main
         - java
            - handler
               - BotLogic.java
            - launcher
                - BotLauncher.java
            - services
                - database
                    - DynamoDB.java
                    - Students.java
                - lexBotConfiguration
                    - BotAlias.java
                    - BotLocale.java
                    - BotVersion.java
                    - Buttons.java
                    - Intent.java
                    - Lex.java
                    - Slot.java
                    - Utterance.java
            - IAM.java
            - Lambda.java
            - S3.java
            - SNS.java
         - resources
            - policies
               - permissions-policy.json
               - trust-policy.json
   - target
      - DBMLauncher-1.0.0.jar
      - original-DBMLauncher-1.0.0.jar
   - DBMSolutionArchitecture.png
   - pom.xml
   - README.md

### Description
The launcher function is used to launch an application into your AWS account.
There will be created IAM role, trust policy, and permissions policy, and that role
will be attached to the created lambda function. That function will be invoked for initialization,
validation, and fulfilling the requirements of the user requests. The only thing you need to configure
by yourself is a chatbot, and connect the already created lambda function with a bot using GUI.

The logic of the chatbot is implemented in BotLogic.java and services DynamoDB, S3, and SNS are implemented in the services folder.
The user will be able to use a chatbot in order to manage Students database table easier. The chatbot provides 4 functions:
get, update, insert, and remove a student.

### Communication between services
Amazon Lex chatbot is connected to the Amazon Lambda function that is triggered every time after the intent is completed.
The chosen action, for example, getting a student from the Students table will be accepted by the chatbot and transmitted
to the lambda function. Then it will send a request to get a student with an ID that the user specified within a conversation.
The chatbot will return all the attributes of the student item including first name, last name, date of birth, classification
(freshman, sophomore, junior, or senior), and email. After this action completes successfully, Lambda will call the Amazon SNS
service to email all subscribers of the topic about the action that was taken on the Students table.

The user may not remember all changes were made within a day and even a week. Therefore, it was decided to log all
actions in the S3 bucket. It is very easy to distinguish between the files that are saved in a bucket because each of them
contains a unique name with an additional class name (GET, INSERT, REMOVE, or UPDATE), and the date it was created.
This feature should help a database administrator to know what happened and at what time with a table.

### Configuration
1. Set up the AWS configuration file.
    1. First way to authenticate to your AWS account using the AWS CLI is to use the configure command.
       This command prompts you to enter 4 attributes:
        1. AWS Access Key
        2. AWS Secret Key
        3. Default Region name
        4. Default Output format

    2. Note that this method requires use of AWS Access and Secret key.  
       To obtain this key set, log into your AWS console and download it for one of your IAM user.
       It is highly recommended that you [don’t use the root user](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#lock-away-credentials) for this type of access use case.
       Instead, [create the IAM user and group](https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html).

    3. Open Terminal application.
    4. Type ```aws configure --profile MyAdmin```.
    5. Enter AWS Access Key ID string.
    6. Enter AWS Secrete Key String
    7. Enter default region code. [See AWS Regions and Endpoints](https://docs.aws.amazon.com/general/latest/gr/rande.html) for valid region codes.
    8. Enter output format.  Valid values are json, text and table.  Note that this value only affects the format of the response on the Terminal application and does not affect any functionality.
    9. At this point a new profile entry is created in your local AWS CLI credentials and config files.

2. Deploy Database Bot Manager Application
   
   Open a command line and enter the command in the following format:
    ```java -jar <accessKey> <secretAccessKey> <awsAppDeploymentRegion> <adminEmail>```

    ```accessKey``` - used to sign programmatic requests that you make to AWS.

    ```secretAccessKey``` - used to sign programmatic requests that you make to AWS.

    ```awsAppDeploymentRegion``` - The [AWS Region](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.RegionsAndAvailabilityZones.html) where the application will be deployed.

    ```adminEmail``` - administrator's email address to which notifications about changes in the database will be sent.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*
