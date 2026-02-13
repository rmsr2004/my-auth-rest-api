package com.myauth.IntegrationTests.Utils.Requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class HttpClient {
    private static String serverAddress;
    private static final Gson gson = new Gson();
    private static String authToken;

    public static <TResponse> HttpResponse<TResponse> get(String endpoint, Class<TResponse> responseType) {
        return doRequest("GET", endpoint, null, null, responseType);
    } 

    public static <TRequest, TResponse> HttpResponse<TResponse> post(String endpoint, TRequest request, Class<TResponse> responseType) {
        return doRequest("POST", endpoint, request, null, responseType);
    }

    public static <TRequest, TResponse> HttpResponse<TResponse> put(String endpoint, TRequest request, Class<TResponse> responseType) {
        return doRequest("PUT", endpoint, request, null, responseType);
    }

    public static <TResponse> HttpResponse<TResponse> delete(String endpoint, String deviceId, Class<TResponse> responseType) {
        return doRequest("DELETE", endpoint, null, deviceId, responseType);
    }

    private static <TRequest, TResponse> HttpResponse<TResponse> doRequest(String method, String endpoint, TRequest request, String deviceId, Class<TResponse> responseType) {
        try {
            URL url = URI.create(serverAddress + endpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            if (authToken != null && !authToken.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + authToken);
            }

            if (deviceId != null && !deviceId.isEmpty()) {
                connection.setRequestProperty("Device-Id", deviceId);
            }

            if (request != null) {
                String jsonRequest = gson.toJson(request);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int statusCode = connection.getResponseCode();
            InputStream responseStream = (statusCode >= 200 && statusCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            } finally {
                connection.disconnect();
            }

            TResponse body = null;
            if (responseType != null && !responseBody.isEmpty()) {
                body = gson.fromJson(responseBody.toString(), responseType);
            }

            return new HttpResponse<>(statusCode, body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void setServerAddress(String serverAddress) {
        HttpClient.serverAddress = serverAddress;
    }

    public static void setAuthToken(String token) {
        authToken = token;
    }
}
