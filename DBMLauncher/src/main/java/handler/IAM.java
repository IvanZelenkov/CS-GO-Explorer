package handler;

import java.util.List;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.iam.waiters.IamWaiter;

public class IAM {
    public static String createIAMRole(IamClient iamClient, String roleName, String fileLocation) throws Exception {
        try {
            IamWaiter iamWaiter = iamClient.waiter();
            JSONObject jsonObject = (JSONObject) readJsonSimpleDemo(fileLocation);
            CreateRoleRequest request = CreateRoleRequest.builder()
                    .roleName(roleName)
                    .assumeRolePolicyDocument(jsonObject.toJSONString())
                    .description("Database Bot Manager Policy")
                    .build();

            CreateRoleResponse response = iamClient.createRole(request);

            GetRoleRequest roleRequest = GetRoleRequest.builder()
                    .roleName(response.role().roleName())
                    .build();

            WaiterResponse<GetRoleResponse> waitUntilRoleExists = iamWaiter.waitUntilRoleExists(roleRequest);
            waitUntilRoleExists.matched().response().ifPresent(System.out::println);

            return response.role().arn();
        } catch (IamException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public static String createIAMPermissionsPolicy(IamClient iamClient, String policyName, String fileLocation) throws Exception {
        try {
            IamWaiter iamWaiter = iamClient.waiter();
            JSONObject jsonObject = (JSONObject) readJsonSimpleDemo(fileLocation);
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
        } catch (IamException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public static void attachIAMRolePermissionsPolicy(IamClient iamClient, String roleName, String permissionsPolicyArn) {
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

            AttachRolePolicyRequest attachRequest = AttachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(permissionsPolicyArn)
                    .build();

            iamClient.attachRolePolicy(attachRequest);
        } catch (IamException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static Object readJsonSimpleDemo(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(reader);
    }
}