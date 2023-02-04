# [CS:GO Explorer](https://main.d1ra5kuot5idbc.amplifyapp.com)

You can use your own Steam ID if you play CS:GO to test the app, or use the following: **76561198070318593**

![CsGoExplorerSolutionArchitecture](https://user-images.githubusercontent.com/64171964/213900837-b70a0ad7-b636-4745-a5fd-112b2f8c3605.png)
![cs-go](https://user-images.githubusercontent.com/64171964/213327049-be01da54-973c-4335-b1da-2cb96f8de9bb.jpeg)

### Description
A CS:GO Explorer app designed to help Counter-Strike players keep track of their stats and view their friends' Steam accounts.
You can see a lot of different information about your friends, including the Steam ID, which you can copy to see their stats and compare with yours.
The statistics will appear in a form of tables, bar charts, and pie charts. This will provide you with a better understanding of your or your friends' stats.

### Communication between services
Communication between the frontend and backend of the application is carried out through the API gateway. AWS Lambda acts as a serverless part of the application
that handles all of the application logic. The frontend of the application will be commited by you to the CodeCommit repository, and every time you are about to update
this repository, AWS Amplify will automatically redeploy the application. If you wish to change the configuration of the API Gateway, then you have 2 options. 
Either use the AWS Management Console directly or change the Swagger CsGoExplorerRestApi.json file that is located at CS-GO-Explorer-main/cs-go-explorer-backend/src/main/resources/swagger 
and upload it to the existing ProductionStage of the API Gateway on the AWS Management Console. There are 4 more AWS services (SNS, DynamoDB, S3, and Lex) that will be used in other planned 
features in future releases.

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
    4. Type ```aws configure --profile <first-name-and-last-name>```.
    5. Enter AWS Access Key ID string.
    6. Enter AWS Secrete Key String
    7. Enter default region code. [See AWS Regions and Endpoints](https://docs.aws.amazon.com/general/latest/gr/rande.html) for valid region codes.
    8. Enter output format.  Valid values are json, text and table.  Note that this value only affects the format of the response on the Terminal application and does not affect any functionality.
    9. At this point a new profile entry is created in your local AWS CLI credentials and config files.

2. Deploy the resources of CS:GO Explorer application to the AWS.

    1. Download the .zip file of the CS-GO-Explorer repository
    2. Unzip the file and open the ```CS-GO-Explorer-main/cs-go-explorer-backend/target``` folder in the Command Prompt or Terminal on your PC.
    3. Run the following command with the required arguments:
       ```java -jar <accessKey> <secretAccessKey> <awsAppDeploymentRegion> <steamApiKey>```

   ```accessKey``` - used to sign programmatic requests that you make to AWS.

   ```secretAccessKey``` - used to sign programmatic requests that you make to AWS.

   ```awsAppDeploymentRegion``` - The [AWS Region](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.RegionsAndAvailabilityZones.html) where the application will be deployed.

   ```steamApiKey``` - API key is a unique identifier used to connect to, or perform, an API call. You can obtain the Steam API key by filling out the form at the following link: [Steam Web API Documentation](https://steamcommunity.com/dev#:~:text=Obtaining%20an%20Steam%20Web%20API%20Key)

    4. Wait until the process is completed. Then you should see the URL of the CodeCommit repository in the following format: ```https://git-codecommit.<aws-region>.amazonaws.com/v1/repos/<repository-name>```. Copy and paste that URL to the notes or archive it on your PC for future use.
    5. Open a Command Prompt or Terminal on your PC and go to some directory using ``cd <directory-name>`` (for example, ```cd Desktop```).
    6. Run ```git init cs-go-explorer-ui``` command.
    7. Run ```cd cs-go-explorer-ui```
    8. Copy all files and folders from ```CS-GO-Explorer-main/cs-go-explorer-frontend/``` and paste them in the newly created cs-go-explorer-ui folder.
    9. Comeback to the Command Prompt or Terminal, and run ```git add .```
    10. Run ```git commit -m "CS:GO Explorer UI version-1.0.0"```
    11. Use that URL you saved earlier and run ```git push https://git-codecommit.<aws-region>.amazonaws.com/v1/repos/<repository-name> --all```
    12. Now you can open your AWS account and search for AWS CodeCommit and AWS Amplify services. You will see that the code of the UI is successfully deployed to the CodeCommit service and the CS:GO Explorer application is currently building. Wait for about 5 minutes until all 3 ticks become green and then click on the URL of the website.
    13. The resources of the application should be successfully deployed to your AWS account and can be fully managed by you. Thank you!

### Support
*itproger.ivan@gmail.com*