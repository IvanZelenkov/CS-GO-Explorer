# Database Bot Manager

### DBMLogic content
- DBMLogic
    - Assignment3.pdf
    - DBMSolutionArchitecture.png
    - pom.xml
    - src
        - main
            - java
               - handler
                  - BotLogic.java
                  - Students.java
               - services
                  - DynamoDB.java
                  - S3.java
                  - SNS.java
    - target
      - DBMLogic-1.0.0.jar

### DBMLauncher content
- DBMLauncher
    - Assignment3.pdf
    - pom.xml
    - src
        - main
            - java
                - handler
                    - Launcher.java
                    - IAM.java
            - resources
                - policies
                    - database-bot-manager-permissions-policy.json
                    - database-bot-manager-trust-policy.json
    - target
      - DBMLauncher-1.0.0.jar

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

2. Set up an IAM role with permissions and an Amazon Lambda.
    
    TODO
   
### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*
