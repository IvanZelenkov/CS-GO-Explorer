package handler.services;

import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.iam.waiters.IamWaiter;

public class IAM {

    /**
     * Creates an IAM role that will be used across the entire application in AWS.
     * @param iamClient Service client for accessing IAM.
     * @param roleName IAM role name.
     * @return Role ARN.
     */
    public static String createRole(IamClient iamClient, String roleName) {
        try {
            IamWaiter iamWaiter = iamClient.waiter();
            JSONObject jsonObject = (JSONObject) readJsonFromFile("trust-policy");
            CreateRoleRequest request = CreateRoleRequest.builder()
                    .roleName(roleName)
                    .assumeRolePolicyDocument(jsonObject.toJSONString())
                    .description("Database Bot Manager Trust Policy")
                    .build();

            CreateRoleResponse response = iamClient.createRole(request);

            // Wait until the role is created.
            GetRoleRequest roleRequest = GetRoleRequest.builder()
                    .roleName(response.role().roleName())
                    .build();

            WaiterResponse<GetRoleResponse> waitUntilRoleExists = iamWaiter.waitUntilRoleExists(roleRequest);
            waitUntilRoleExists.matched().response().ifPresent(System.out::println);

            return response.role().arn();
        } catch (IamException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Creates a service-linked role for AWS Lex V2.
     * @param iamClient Service client for accessing IAM.
     * @param awsServiceName The service principal for the AWS service to which this role is attached.
     * @param customSuffix A string that you provide, which is combined with the service-provided prefix to form the complete role name.
     * @param description The description of the role.
     * @return Service-linked role ARN.
     */
    public static String createServiceLinkedRole(IamClient iamClient, String awsServiceName, String customSuffix, String description) {
        CreateServiceLinkedRoleRequest createServiceLinkedRoleRequest = CreateServiceLinkedRoleRequest
                .builder()
                .awsServiceName(awsServiceName)
                .customSuffix(customSuffix)
                .description(description)
                .build();

        CreateServiceLinkedRoleResponse createServiceLinkedRoleResponse = iamClient.createServiceLinkedRole(createServiceLinkedRoleRequest);

        GetRoleRequest getRoleRequest = GetRoleRequest
                .builder()
                .roleName(createServiceLinkedRoleResponse.role().roleName())
                .build();

        GetRoleResponse getRoleResponse = iamClient.getRole(getRoleRequest);

        return getRoleResponse.role().arn();
    }

    /**
     * Creates permissions policy.
     * @param iamClient Service client for accessing IAM.
     * @param policyName Policy name.
     * @return Policy ARN.
     */
    public static String createPermissionsPolicy(IamClient iamClient, String policyName) {
        try {
            IamWaiter iamWaiter = iamClient.waiter();
            JSONObject jsonObject = (JSONObject) readJsonFromFile("permissions-policy");
            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .policyName(policyName)
                    .policyDocument(jsonObject.toJSONString())
                    .build();

            CreatePolicyResponse response = iamClient.createPolicy(request);

            // Wait until the policy is created.
            GetPolicyRequest policyRequest = GetPolicyRequest.builder()
                    .policyArn(response.policy().arn())
                    .build();

            WaiterResponse<GetPolicyResponse> waitUntilPolicyExists = iamWaiter.waitUntilPolicyExists(policyRequest);
            waitUntilPolicyExists.matched().response().ifPresent(System.out::println);

            return response.policy().arn();
        } catch (IamException | IOException | ParseException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     *
     * @param iamClient Service client for accessing IAM.
     * @param roleName Name of the role to which permissions policy will be attached.
     * @param permissionsPolicyArn Permissions policy ARN.
     */
    public static void attachRolePermissionsPolicy(IamClient iamClient, String roleName, String permissionsPolicyArn) {
        try {
            ListAttachedRolePoliciesRequest request = ListAttachedRolePoliciesRequest.builder()
                    .roleName(roleName)
                    .build();

            ListAttachedRolePoliciesResponse response = iamClient.listAttachedRolePolicies(request);
            List<AttachedPolicy> attachedPolicies = response.attachedPolicies();

            // Ensure that the policy is not attached to this role
            String policyArn;
            for (AttachedPolicy policy : attachedPolicies) {
                policyArn = policy.policyArn();
                if (policyArn.compareTo(permissionsPolicyArn) == 0) {
                    System.out.println(roleName + " policy is already attached to this role.");
                    return;
                }
            }

            AttachRolePolicyRequest attachPolicyRequest = AttachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(permissionsPolicyArn)
                    .build();

            iamClient.attachRolePolicy(attachPolicyRequest);
        } catch (IamException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Reads JSON file content then converts it to the InputStream and parses to JSONObject.
     * @param filename The name of the JSON file to read.
     * @return Returns the parsed contents of the JSON file as a JSONObject.
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
     * @throws ParseException Signals that an error has been reached unexpectedly while parsing.
     */
    private static Object readJsonFromFile(String filename) throws IOException, ParseException {
        InputStream inputStream = IAM.class.getResourceAsStream("/policies/" + filename + ".json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(bufferedReader);
    }
}