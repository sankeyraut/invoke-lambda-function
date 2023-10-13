package com.example.lambda;

import java.time.Duration;

import org.json.JSONObject;

import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
// snippet-end:[lambda.java2.invoke.import]

public class Invoke {

    public static void main(String[] args) {

        String functionName = "WaitFunction";
        Region region = Region.US_WEST_2;
        LambdaClient awsLambda = LambdaClient.builder()
                .region(region)
                .httpClientBuilder(ApacheHttpClient.builder()
                        .maxConnections(100)
                        .socketTimeout(Duration.ofMillis(310000)))
                .overrideConfiguration(
                        b -> b.apiCallTimeout(Duration.ofMillis(310000)))

                .build();

        invokeFunction(awsLambda, functionName);
        awsLambda.close();
    }

    // snippet-start:[lambda.java2.invoke.main]
    public static void invokeFunction(LambdaClient awsLambda, String functionName) {

        InvokeResponse res = null;
        try {
            // Need a SdkBytes instance for the payload.
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("inputValue", "2000");
            String json = jsonObj.toString();
            SdkBytes payload = SdkBytes.fromUtf8String(json);

            // Setup an InvokeRequest.
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(payload)
                    .build();
            long start = System.currentTimeMillis();
            res = awsLambda.invoke(request);
            System.out.println("Time Taken: " + (System.currentTimeMillis() - start));
            String value = res.payload().asUtf8String();
            System.out.println(value);

        } catch (LambdaException e) {
            System.err.println(e.getMessage());
        }
    }

}
