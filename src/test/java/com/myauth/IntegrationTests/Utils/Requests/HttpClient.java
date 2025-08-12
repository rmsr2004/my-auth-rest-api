package com.myauth.IntegrationTests.Utils.Requests;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class HttpClient {
    private static String serverAddress;
    private static final Gson gson = new Gson();

    public static <TRequest, TResponse> HttpResponse<TResponse>  post(String endpoint, TRequest request, Class<TResponse> responseType) {
        return doRequest("POST", endpoint, request, responseType);
    }

    private static <TRequest, TResponse> HttpResponse<TResponse> doRequest(String method, String endpoint, TRequest request, Class<TResponse> responseType) {
        try {
            URL url = URI.create(serverAddress + endpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

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
}
