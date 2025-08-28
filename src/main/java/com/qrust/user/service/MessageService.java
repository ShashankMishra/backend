package com.qrust.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MessageService {

    @ConfigProperty(name = "messageCentralAuthToken")
    String messageCentralAuthToken;

    public String sendOtp(String phoneNumber) throws IOException {
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://cpaas.messagecentral.com/verification/v3/send?countryCode=91&customerId=C-0BEB1E48AB744D2&flowType=SMS&mobileNumber=" + phoneNumber)
                .method("POST", body)
                .addHeader("authToken", messageCentralAuthToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);
                JsonNode dataNode = root.get("data");
                if (dataNode != null && dataNode.has("verificationId")) {
                    return dataNode.get("verificationId").asText();
                }
            }
        }

        return "";
    }

    public boolean validateOtp(String phoneNumber, String otp, String verificationId) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url = "https://cpaas.messagecentral.com/verification/v3/validateOtp?countryCode=91&mobileNumber=" + phoneNumber + "&verificationId=" + verificationId + "&customerId=C-0BEB1E48AB744D2&code=" + otp;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("authToken", messageCentralAuthToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);
                JsonNode dataNode = root.get("data");
                if (dataNode != null) {
                    JsonNode statusNode = dataNode.get("verificationStatus");
                    if (statusNode != null && "VERIFICATION_COMPLETED".equals(statusNode.asText())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
