## cs-go-explorer-backend

### Content
- cs-go-explorer-backend
   - src
      - main
         - java
            - handler
                - AppHandler.java
            - launcher
                - AppLauncher.java
            - services
                - api
                    - steam
                        - SteamApi.java
                    - ApiGateway.java
                    - ApiGatewayProxyRequest.java
                    - ApiGatewayProxyResponse.java
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
            - Amplify.java
            - CodeCommit.java
            - IAM.java
            - Lambda.java
            - S3.java
            - SNS.java
         - resources
            - policies
               - permissions-policy.json
               - trust-policy.json
            - swagger
               - steam-client-rest-api.json
   - target
      - cs-go-explorer-backend-1.0.0.jar
      - original-cs-go-explorer-backend-1.0.0.jar
   - pom.xml
   - README.md

### Description
TODO

### Communication between services
TODO

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

2. Deploy Steam Client application
    Open a command line and enter the command in the following format:
    ```java -jar <accessKey> <secretAccessKey> <awsAppDeploymentRegion> <adminEmail> <steamId> <steamApiKey>```

    ```accessKey``` - used to sign programmatic requests that you make to AWS.

    ```secretAccessKey``` - used to sign programmatic requests that you make to AWS.

    ```awsAppDeploymentRegion``` - The [AWS Region](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.RegionsAndAvailabilityZones.html) where the application will be deployed.

    ```userEmail``` - user's email address to which notifications about changes in the database will be sent.

    ```steamId``` - unique identifier of your Steam account.

    ```steamApiKey``` - API key is a unique identifier used to connect to, or perform, an API call.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*