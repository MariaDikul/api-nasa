package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class Main {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=KV0mpsBrADvN5KFlJhZNkkaJ7muFR2odeaTfQDTu");
        CloseableHttpResponse response = httpClient.execute(request);
        ServerResponse serverResponse = mapper.readValue(response.getEntity().getContent(), new TypeReference<ServerResponse>() {
        });

        HttpGet requestUrl = new HttpGet(serverResponse.getUrl());
        CloseableHttpResponse responseUrl = httpClient.execute(requestUrl);

        String[] splitUrl = serverResponse.getUrl().split("/");

        try (FileOutputStream fos = new FileOutputStream(splitUrl[splitUrl.length - 1])) {
            byte[] bytes = new byte[1024];
            int read;
            while ((read = responseUrl.getEntity().getContent().read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpClient.close();
    }
}