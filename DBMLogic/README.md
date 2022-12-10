# Cloud Computing

## DBMLogic

### Content
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

### Description
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

Of course, the user may not remember all changes were made within a day and even a week. Therefore, it was decided to log all
actions in the S3 bucket. It is very easy to distinguish between the files that are saved in a bucket because each of them
contains a unique name with an additional class name (GET, INSERT, REMOVE, or UPDATE), and the date it was created.
This feature should help a database administrator to know what happened and at what time with a table.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*