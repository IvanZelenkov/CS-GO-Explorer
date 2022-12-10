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
    1. To run the command you will need to provide 10 additional arguments: accessKey secretAccessKey adminName roleName trustPolicyFileLocation permissionsPolicyName 
   permissionsPolicyFileLocation lambdaFunctionName lambdaFilePath handler
       1. accessKey - used to sign programmatic requests that you make to AWS.
       2. secretAccessKey - used to sign programmatic requests that you make to AWS.
       3. adminName - The name of the administrator who will manage the table.
       4. roleName - The name of the IAM role to be created.
       5. trustPolicyFileLocation - The path to the JSON where the trust policy is located.
       6. permissionsPolicyName - The name of the permissions policy.
       7. permissionsPolicyFileLocation - The path to the JSON where the permissions policy is located.
       8. lambdaFunctionName - The name of the Lambda function.
       9. lambdaFilePath - The path to the ZIP or JAR where the code is located.
       10. handler - The fully qualified method name (for example, example.Handler::handleRequest).
    2. Copy and paste a JAR file from DBMLauncher which is located at /DBMLauncher/target/DBMLauncher-1.0.0.jar somewhere on your PC.
    3. Copy and paste a JAR file from DBMLogic which is located at /DBMLogic/target/DBMLogic-1.0.0.jar somewhere on your PC.
    4. Get the absolute path of the DBMLogic-1.0.0.jar file and save it in your notes for later use.
    5. Go to the /DBMLaucher/src/main/recourses/policies, get the absolute paths of database-bot-manager-permissions-policy.json and database-bot-manager-trust-policy.json files, and save it in your notes for later use.
    6. Open the Command Line and cd to the directory where DBMLauncher-1.0.0.jar is located. 
    7. Follow the above rules to execute the command correctly. Here is an example of what I used when testing:
   
       ```java -jar DBMLauncher-1.0.0.jar <accessKey> <secretAccessKey> "Ivan Zelenkov" database-bot-manager-lambda-role /Users/ivanzelenkov/IdeaProjects/DBMLauncher/src/main/resources/policies/database-bot-manager-trust-policy.json database-bot-manager-permissions-policy /Users/ivanzelenkov/IdeaProjects/DBMLauncher/src/main/resources/policies/database-bot-manager-permissions-policy.json database-bot-manager-lambda /Users/ivanzelenkov/Desktop/DBMLogic-1.0.0.jar handler.BotLogic::handleRequest```
     
    8. There can be an issue when you will run this command (IAM policy may not be attached when creating lambda function call will happen). 
   The following message can be displayed: ```The role defined for the function cannot be assumed by Lambda```.

    9. If that happens, remove the role, and permissions policy that you named, and rerun the command.

4. Set up an Amazon Lex chatbot.
   1. Initial Configuration
      1. Go to the AWS Amazon Lex webpage.
      2. Click 'Create bot'
      3. Enter the name of the bot you want to create.
      4. Select 'Create a role with basic Amazon Lex permissions'.
      5. Is use of your bot subject to the Children’s Online Privacy Protection Act (COPPA)? Select 'No'
      6. Click 'Next'
      7. You can leave the 'Add language to bot' settings as default and click 'Done'.
   
   2. Create 'Greeting' intent.
      1. Change an intent name to 'Greeting'.
      2. Scroll down and add sample utterances: Hi, Hello, Good morning, Good afternoon, Good evening, Sure, Yes, Restart, Start over.
      3. Scroll down to the 'Fulfillment', then click 'Advanced Options'.
      4. Mark 'Use a Lambda function for fulfillment' on a 'Fulfillment Lambda code hook'. 
      5. Go back and click 'Save intent'.
   
   3. Create 'Classification' slot type.
      1. Click 'Slot types' on the left pane.
      2. Select 'Add slot type'.
      3. Click 'Add blank slot type'.
      4. Enter 'Classification' as a 'Slot type name'.
      5. Enter 4 slot type values: Freshman, Sophomore, Junior, Senior.
      6. Click 'Save Slot type'.
   
   4. Create 'InsertStudent' intent.
      1. Click 'Intents' on the left pane.
      2. Click 'Add Intent'.
      3. Select 'Add empty intent'.
      4. Enter the intent name 'InsertStudent'.
      5. Scroll down and add sample utterance: Insert.
      6. Scroll down and you will see 'Slots' section, click 'Add slot'.
      7. Enter 'Name': StudentID
      8. Select 'Slot type': AMAZON.Number.
      9. Enter the prompt message: 'Please provide me a student ID.'
      10. Click 'Add'.
      11. Then, click 'Add slot' to add another slot.
      12. Enter 'Name': FirstName
      13. Select 'Slot type': AMAZON.FirstName.
      14. Enter the prompt message: 'Student's first name.'.
      15. Click 'Add'.
      16. Then, click 'Add slot' to add another slot.
      17. Enter 'Name': LastName
      18. Select 'Slot type': AMAZON.LastName.
      19. Enter the prompt message: 'Last name.'.
      20. Click 'Add'.
      21. Then, click 'Add slot' to add another slot.
      22. Enter 'Name': DateOfBirth
      23. Select 'Slot type': AMAZON.Date.
      24. Enter the prompt message: 'Date of birth (MM/DD/YYYY).'.
      25. Click 'Add'.
      26. Then, click 'Add slot' to add another slot.
      27. Enter 'Name': Classification
      28. Enter the prompt message: 'Classification'.
      29. Select 'Slot type' and choose a custom slot type 'Classification'.
      30. Click on the 'Classification' slot you just created.
      31. Click 'Advanced Options'.
      32. There is a 'Slot prompts' section, click on 'Bot elicits information'.
      33. Click 'More prompt options'.
      34. From the 'Slot prompts' on the right, click 'Add', and choose 'Add card group'.
      35. You will see that the Card group was created. Remove Message group above that is above Card group.
      36. Enter 'Title': Student Classification.
      37. Click 'Add button'.
      38. Enter 'Button 1 title': Freshman, and 'Button 1 value': Freshman.
      39. Enter 'Button 2 title': Sophomore, and 'Button 2 value': Sophomore.
      40. Enter 'Button 3 title': Junior, and 'Button 3 value': Junior.
      41. Enter 'Button 4 title': Senior, and 'Button 4 value': Senior.
      42. Click 'Update prompts'.
      43. Click 'Update slot'.
      44. Then, click 'Add slot' to add another slot.
      45. Enter 'Name': Email
      46. Select 'Slot type': AMAZON.EmailAddress.
      47. Enter the prompt message: 'Student's email address.'.
      48. Click 'Add'.
      49. Scroll down to the 'Fulfillment', then click 'Advanced Options'.
      50. Mark 'Use a Lambda function for fulfillment' on a 'Fulfillment Lambda code hook'.
      51. Go back and click 'Save Intent'.
      52. Click 'Save intent'.
      53. Click 'Back to intents list' on the left pane.
      
   5. Create 'RemoveStudent' intent.
      1. Click 'Add Intent'.
      2. Select 'Add empty intent'.
      3. Enter the intent name 'RemoveStudent'.
      4. Scroll down and add sample utterance: Remove.
      5. Scroll down and you will see 'Slots' section, click 'Add slot'.
      6. Enter 'Name': StudentID
      7. Select 'Slot type': AMAZON.Number.
      8. Enter the prompt message: 'Please enter the student ID.'
      9. Click 'Add'.
      10. Scroll down to the 'Fulfillment', then click 'Advanced Options'.
      11. Mark 'Use a Lambda function for fulfillment' on a 'Fulfillment Lambda code hook'.
      12. Go back and click 'Save Intent'.
      13. Click 'Back to intents list' on the left pane.
      
   6. Create 'GetStudent' intent.
      1. Click 'Add Intent'.
      2. Select 'Add empty intent'.
      3. Enter the intent name 'GetStudent'.
      4. Scroll down and add sample utterance: Get. 
      5. Scroll down to the 'Fulfillment', then click 'Advanced Options'.
      6. Mark 'Use a Lambda function for fulfillment' on a 'Fulfillment Lambda code hook'.
      7. Go back and click 'Save Intent'.
      8. Click 'Back to intents list' on the left pane.
   
   7. Create 'UpdateStudent' intent.
       1. Click 'Add Intent'.
       2. Select 'Add empty intent'.
       3. Enter the intent name 'UpdateStudent'.
       4. Scroll down and add sample utterance: Update.
       5. Scroll down and you will see 'Slots' section, click 'Add slot'.
       6. Enter 'Name': StudentID
       7. Select 'Slot type': AMAZON.Number.
       8. Enter the prompt message: 'Please enter the student ID to find a person.'
       9. Click 'Add'.
       10. Then, click 'Add slot' to add another slot.
       11. Enter 'Name': AttributeName
       12. Select 'Slot type': AMAZON.AlphaNumeric.
       13. Enter the prompt message: 'Which student attribute would you like to update?'.
       14. Click 'Add'.
       15. Then, click 'Add slot' to add another slot.
       16. Enter 'Name': NewAttributeValue
       17. Select 'Slot type': AMAZON.FreeFormInput.
       18. Enter the prompt message: 'Enter a new attribute value.'.
       19. Click 'Add'.
       20. Scroll down to the 'Fulfillment', then click 'Advanced Options'.
       21. Mark 'Use a Lambda function for fulfillment' on a 'Fulfillment Lambda code hook'.
       22. Go back and click 'Save Intent'.
       23. Click 'Back to intents list' on the left pane.
      
   8. Create 'AnotherActionRejected' intent.
      1. Click 'Add Intent'.
      2. Select 'Add empty intent'.
      3. Enter the intent name 'GetStudent'.
      4. Scroll down and add sample utterances: No, No action, Bye, 'No, Thank you'.
      5. Scroll down to the 'Code hooks'.
      6. Mark 'Use a Lambda function for initialization and validation'.
      7. Click 'Save Intent'.
      8. Click 'Back to intents list' on the left pane.

   9. Connect an Amazon Lex bot with a lambda function containing 
   the bot's logic and the ability to connect to other services.
      1. Click the 'Test' button at the top of the webpage, and the chatbot window will be opened.
      2. Click on the gearwheel at the top of the chat window.
      3. Select the lambda function you created earlier.
      4. Click 'Save'.
      5. Click the "Create" button at the top of the web page and wait about a minute for the bot to be created.

   10. The bot should have 7 intents, and 1 custom slot type. Whenever you will be updating
       a student record enter an exact attribute name that is in a Students table. For example,
       if you want to update a student's first name, you will need to enter the exact name of
       the attribute - 'firstName', the same idea for the rest of the attributes.
   
   11. Now, you can say 'Hello' to the bot and start populating and modifying the Students table.
   
### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*
