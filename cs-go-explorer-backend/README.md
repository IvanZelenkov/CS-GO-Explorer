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
            - amplify-build-spec
                - amplify-build-spec.yml
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

### Communication between services
Communication between the frontend and backend of the application is carried out through the API gateway. AWS Lambda acts as a serverless part of the application 
that handles all of the application logic. The frontend of the application will be commited by you to the CodeCommit repository, and every time you are about to update 
this repository, AWS Amplify will automatically redeploy the application. There are 4 more AWS services (SNS, DynamoDB, S3, and Lex) that will be used in other planned 
features in future releases.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*