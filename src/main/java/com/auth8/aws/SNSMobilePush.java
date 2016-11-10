package com.auth8.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.auth8.aws.SNSMessageGenerator.Platform;
import com.auth8.persistent.Device;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tarka
 */
public class SNSMobilePush {

    private final SNSClientWrapper snsClientWrapper;
//    private final String regid = "APA91bEB2eh6-k7rIqa8RWoW02dUraSIvR3LCjMq6XqqR98IRkYvTMcop_oE-frA_YzP6FwInvAgwF3srhyfvHP1zChlAoBi9iLAOiHNj9SZLJ-BzD_zcMQCRdWMeVGKJWFi2AwN9KP_J4r3vsXJ0fVO3KkcxBVhUbJiY82dLSygBMjiDzutRvs";

    public SNSMobilePush(AmazonSNS snsClient) {
        this.snsClientWrapper = new SNSClientWrapper(snsClient);
    }

    public static final Map<Platform, Map<String, MessageAttributeValue>> attributesMap = new HashMap<>();

    static {
        attributesMap.put(Platform.ADM, null);
        attributesMap.put(Platform.GCM, null);
        attributesMap.put(Platform.APNS, null);
        attributesMap.put(Platform.APNS_SANDBOX, null);
        attributesMap.put(Platform.BAIDU, addBaiduNotificationAttributes());
        attributesMap.put(Platform.WNS, addWNSNotificationAttributes());
        attributesMap.put(Platform.MPNS, addMPNSNotificationAttributes());
    }

    public static void sendMessage(Device device, String requestId, Map<String, String> payload) throws IOException {
        /*
         * TODO: Be sure to fill in your AWS access credentials in the
         * AwsCredentials.properties file before you try to run this sample.
         * http://aws.amazon.com/security-credentials
         */
        AmazonSNS sns = new AmazonSNSClient(new PropertiesCredentials(
                Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("AwsCredentials.properties")));

        sns.setEndpoint("https://sns.us-east-1.amazonaws.com");

        try {
            SNSMobilePush sample = new SNSMobilePush(sns);
            /* TODO: Uncomment the services you wish to use. */
            sample.demoAndroidAppNotification(device, requestId, payload);
            // sample.demoKindleAppNotification();
            // sample.demoAppleAppNotification();
            // sample.demoAppleSandboxAppNotification();
            // sample.demoBaiduAppNotification();
            // sample.demoWNSAppNotification();
            // sample.demoMPNSAppNotification();
        } catch (AmazonServiceException ase) {
            System.out
                    .println("Caught an AmazonServiceException, which means your request made it "
                            + "to Amazon SNS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out
                    .println("Caught an AmazonClientException, which means the client encountered "
                            + "a serious internal problem while trying to communicate with SNS, such as not "
                            + "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    public void demoAndroidAppNotification(Device device, String requestId, Map<String, String> payload) {
        // TODO: Please fill in following values for your application. You can
        // also change the notification payload as per your preferences using
        // the method
        // com.amazonaws.sns.samples.tools.SampleMessageGenerator.getSampleAndroidMessage()
        String serverAPIKey = "AIzaSyBbfP8iK3hGpiOPeHf6ycAIUSj-V0jEgqg";
        String applicationName = "Auth8";
        String registrationId = device.getRegid();
        snsClientWrapper.demoNotification(
                Platform.GCM, 
                "", 
                serverAPIKey,
                registrationId, 
                applicationName, 
                attributesMap,
                requestId,
                payload);
    }

    public void demoKindleAppNotification() {
        // TODO: Please fill in following values for your application. You can
        // also change the notification payload as per your preferences using
        // the method
        // com.amazonaws.sns.samples.tools.SampleMessageGenerator.getSampleKindleMessage()
//        String clientId = "";
//        String clientSecret = "";
//        String applicationName = "";
//
//        String registrationId = "";
//        snsClientWrapper.demoNotification(Platform.ADM, clientId, clientSecret,
//                registrationId, applicationName, attributesMap);
    }

    public void demoAppleAppNotification() {
        // TODO: Please fill in following values for your application. You can
        // also change the notification payload as per your preferences using
        // the method
        // com.amazonaws.sns.samples.tools.SampleMessageGenerator.getSampleAppleMessage()
//        String certificate = ""; // This should be in pem format with \n at the
//        // end of each line.
//        String privateKey = ""; // This should be in pem format with \n at the
//        // end of each line.
//        String applicationName = "";
//        String deviceToken = ""; // This is 64 hex characters.
//        snsClientWrapper.demoNotification(Platform.APNS, certificate,
//                privateKey, deviceToken, applicationName, attributesMap);
    }

    public void demoAppleSandboxAppNotification() {
        // TODO: Please fill in following values for your application. You can
        // also change the notification payload as per your preferences using
        // the method
        // com.amazonaws.sns.samples.tools.SampleMessageGenerator.getSampleAppleMessage()
//        String certificate = ""; // This should be in pem format with \n at the
//        // end of each line.
//        String privateKey = ""; // This should be in pem format with \n at the
//        // end of each line.
//        String applicationName = "";
//        String deviceToken = ""; // This is 64 hex characters.
//        snsClientWrapper.demoNotification(Platform.APNS_SANDBOX, certificate,
//                privateKey, deviceToken, applicationName, attributesMap);
    }

    public void demoBaiduAppNotification() {
        /*
         * TODO: Please fill in the following values for your application. If
         * you wish to change the properties of your Baidu notification, you can
         * do so by modifying the attribute values in the method
         * addBaiduNotificationAttributes() . You can also change the
         * notification payload as per your preferences using the method
         * com.amazonaws
         * .sns.samples.tools.SampleMessageGenerator.getSampleBaiduMessage()
         */
//        String userId = "";
//        String channelId = "";
//        String apiKey = "";
//        String secretKey = "";
//        String applicationName = "";
//        snsClientWrapper.demoNotification(Platform.BAIDU, apiKey, secretKey,
//                channelId + "|" + userId, applicationName, attributesMap);
    }

    public void demoWNSAppNotification() {
        /*
         * TODO: Please fill in the following values for your application. If
         * you wish to change the properties of your WNS notification, you can
         * do so by modifying the attribute values in the method
         * addWNSNotificationAttributes() . You can also change the notification
         * payload as per your preferences using the method
         * com.amazonaws.sns.samples
         * .tools.SampleMessageGenerator.getSampleWNSMessage()
         */
//        String notificationChannelURI = "";
//        String packageSecurityIdentifier = "";
//        String secretKey = "";
//        String applicationName = "";
//        snsClientWrapper.demoNotification(Platform.WNS,
//                packageSecurityIdentifier, secretKey, notificationChannelURI,
//                applicationName, attributesMap);
    }

    public void demoMPNSAppNotification() {
        /*
         * TODO: Please fill in the following values for your application. If
         * you wish to change the properties of your MPNS notification, you can
         * do so by modifying the attribute values in the method
         * addMPNSNotificationAttributes() . You can also change the
         * notification payload as per your preferences using the method
         * com.amazonaws
         * .sns.samples.tools.SampleMessageGenerator.getSampleMPNSMessage ()
         */
//        String notificationChannelURI = "";
//        String applicationName = "";
//        snsClientWrapper.demoNotification(Platform.MPNS, "", "",
//                notificationChannelURI, applicationName, attributesMap);
    }

    private static Map<String, MessageAttributeValue> addBaiduNotificationAttributes() {
        Map<String, MessageAttributeValue> notificationAttributes = new HashMap<>();
        notificationAttributes.put("AWS.SNS.MOBILE.BAIDU.DeployStatus",
                new MessageAttributeValue().withDataType("String")
                .withStringValue("1"));
        notificationAttributes.put("AWS.SNS.MOBILE.BAIDU.MessageKey",
                new MessageAttributeValue().withDataType("String")
                .withStringValue("default-channel-msg-key"));
        notificationAttributes.put("AWS.SNS.MOBILE.BAIDU.MessageType",
                new MessageAttributeValue().withDataType("String")
                .withStringValue("0"));
        return notificationAttributes;
    }

    private static Map<String, MessageAttributeValue> addWNSNotificationAttributes() {
        Map<String, MessageAttributeValue> notificationAttributes = new HashMap<>();
        notificationAttributes.put("AWS.SNS.MOBILE.WNS.CachePolicy",
                new MessageAttributeValue().withDataType("String")
                .withStringValue("cache"));
        notificationAttributes.put("AWS.SNS.MOBILE.WNS.Type",
                new MessageAttributeValue().withDataType("String")
                .withStringValue("wns/badge"));
        return notificationAttributes;
    }

    private static Map<String, MessageAttributeValue> addMPNSNotificationAttributes() {
        Map<String, MessageAttributeValue> notificationAttributes = new HashMap<>();
        notificationAttributes.put("AWS.SNS.MOBILE.MPNS.Type",
                new MessageAttributeValue().withDataType("String")
                .withStringValue("token")); // This attribute is required.
        notificationAttributes.put("AWS.SNS.MOBILE.MPNS.NotificationClass",
                new MessageAttributeValue().withDataType("String")
                .withStringValue("realtime")); // This attribute is required.

        return notificationAttributes;
    }
}
