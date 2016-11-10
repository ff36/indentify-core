package com.auth8.aws;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.auth8.aws.SNSMessageGenerator.Platform;
import static com.auth8.aws.SNSMessageGenerator.jsonify;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tarka
 */
public class SNSClientWrapper {

    private final AmazonSNS snsClient;

    public SNSClientWrapper(AmazonSNS client) {
        this.snsClient = client;
    }

    private CreatePlatformApplicationResult createPlatformApplication(
            String applicationName, Platform platform, String principal,
            String credential) {
        CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("PlatformPrincipal", principal);
        attributes.put("PlatformCredential", credential);
        platformApplicationRequest.setAttributes(attributes);
        platformApplicationRequest.setName(applicationName);
        platformApplicationRequest.setPlatform(platform.name());
        return snsClient.createPlatformApplication(platformApplicationRequest);
    }

    private CreatePlatformEndpointResult createPlatformEndpoint(
            Platform platform, String customData, String platformToken,
            String applicationArn) {
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
        platformEndpointRequest.setCustomUserData(customData);
        String token = platformToken;
        String userId = null;
        if (platform == Platform.BAIDU) {
            String[] tokenBits = platformToken.split("\\|");
            token = tokenBits[0];
            userId = tokenBits[1];
            Map<String, String> endpointAttributes = new HashMap<>();
            endpointAttributes.put("UserId", userId);
            endpointAttributes.put("ChannelId", token);
            platformEndpointRequest.setAttributes(endpointAttributes);
        }
        platformEndpointRequest.setToken(token);
        platformEndpointRequest.setPlatformApplicationArn(applicationArn);
        return snsClient.createPlatformEndpoint(platformEndpointRequest);
    }

    private void deletePlatformApplication(String applicationArn) {
        DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
        request.setPlatformApplicationArn(applicationArn);
        snsClient.deletePlatformApplication(request);
    }

    private PublishResult publish(
            String endpointArn,
            Platform platform,
            Map<Platform, Map<String, MessageAttributeValue>> attributesMap,
            String requestId,
            Map<String, String> payload) {

        PublishRequest publishRequest = new PublishRequest();
        Map<String, MessageAttributeValue> notificationAttributes = getValidNotificationAttributes(attributesMap
                .get(platform));
        if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
            publishRequest.setMessageAttributes(notificationAttributes);
        }
        publishRequest.setMessageStructure("json");
        // If the message attributes are not set in the requisite method,
        // notification is sent with default attributes
        String message = getPlatformSampleMessage(platform, requestId, payload);
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put(platform.name(), message);
        message = SNSMessageGenerator.jsonify(messageMap);
        // For direct publish to mobile end points, topicArn is not relevant.
        publishRequest.setTargetArn(endpointArn);

        // Display the message that will be sent to the endpoint/
        System.out.println("{Message Body: " + message + "}");
        StringBuilder builder = new StringBuilder();
        builder.append("{Message Attributes: ");
        for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes
                .entrySet()) {
            builder.append("(\"" + entry.getKey() + "\": \""
                    + entry.getValue().getStringValue() + "\"),");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        System.out.println(builder.toString());

        publishRequest.setMessage(message);
        return snsClient.publish(publishRequest);
    }

    public void demoNotification(
            Platform platform,
            String principal,
            String credential,
            String platformToken,
            String applicationName,
            Map<Platform, Map<String, MessageAttributeValue>> attrsMap,
            String requestId,
            Map<String, String> payload) {
        // Create Platform Application. This corresponds to an app on a
        // platform.
        CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
                applicationName, platform, principal, credential);
        System.out.println(platformApplicationResult);

        // The Platform Application Arn can be used to uniquely identify the
        // Platform Application.
        String platformApplicationArn = platformApplicationResult
                .getPlatformApplicationArn();

        // Create an Endpoint. This corresponds to an app on a device.
        CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(
                platform,
                "CustomData - Useful to store endpoint specific data",
                platformToken, platformApplicationArn);
        System.out.println(platformEndpointResult);

        // Publish a push notification to an Endpoint.
        PublishResult publishResult = publish(
                platformEndpointResult.getEndpointArn(),
                platform,
                attrsMap,
                requestId,
                payload);
        System.out.println("Published! \n{MessageId="
                + publishResult.getMessageId() + "}");
        // Delete the Platform Application since we will no longer be using it.
        deletePlatformApplication(platformApplicationArn);
    }

    private String getPlatformSampleMessage(Platform platform, String requestId, Map<String, String> payload) {
        switch (platform) {
            case APNS:
                return SNSMessageGenerator.getSampleAppleMessage();
            case APNS_SANDBOX:
                return SNSMessageGenerator.getSampleAppleMessage();
            case GCM:
                return getAndroidMessage(requestId, payload);
            case ADM:
                return SNSMessageGenerator.getSampleKindleMessage();
            case BAIDU:
                return SNSMessageGenerator.getSampleBaiduMessage();
            case WNS:
                return SNSMessageGenerator.getSampleWNSMessage();
            case MPNS:
                return SNSMessageGenerator.getSampleMPNSMessage();
            default:
                throw new IllegalArgumentException("Platform not supported : "
                        + platform.name());
        }
    }

    public static Map<String, MessageAttributeValue> getValidNotificationAttributes(
            Map<String, MessageAttributeValue> notificationAttributes) {

        Map<String, MessageAttributeValue> validAttributes = new HashMap<>();
        if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
            for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes
                    .entrySet()) {
                if (!StringUtils.isBlank(entry.getValue().getStringValue())) {
                    validAttributes.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return validAttributes;
    }

    public static String getAndroidMessage(String requestId, Map<String, String> payload) {
        Map<String, Object> androidMessageMap = new HashMap<>();
        androidMessageMap.put("collapse_key", requestId);
        androidMessageMap.put("data", payload);
        androidMessageMap.put("delay_while_idle", true);
        androidMessageMap.put("time_to_live", 125);
        androidMessageMap.put("dry_run", false);
        return jsonify(androidMessageMap);
    }
}
