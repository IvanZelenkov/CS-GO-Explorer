## DBMLauncher

### Content
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

### Description
The launcher function is used to launch an application into your AWS account. 
There will be created IAM role, trust policy, and permissions policy, and that role 
will be attached to the created lambda function. That function will be invoked for initialization, 
validation, and fulfilling the requirements of the user requests. The only thing you need to configure 
by yourself is a chatbot, and connect the already created lambda function with a bot using GUI.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*
