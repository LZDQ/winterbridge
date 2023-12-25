package net.lzdq.winterbridge.network;

import com.google.gson.Gson;
import net.lzdq.winterbridge.BridgeMod;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ToPython {
    public static void send(Object req){
        Gson gson = new Gson();
        String jsonContent = gson.toJson(req);
        BridgeMod.LOGGER.info(jsonContent);

        try{
            String url = "http://127.0.0.1:1234/";
            // TODO: add command line configure
            //BridgeMod.LOGGER.info("Line 1");
            HttpClient client = HttpClient.newHttpClient();
            //BridgeMod.LOGGER.info("Line 2");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonContent))
                    .build();
            //BridgeMod.LOGGER.info("Line 3");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //BridgeMod.LOGGER.info("Line 4");
            // Print response
            //System.out.println("Response status code: " + response.statusCode());
            //System.out.println("Response body: " + response.body());
            //BridgeMod.LOGGER.info(String.format("%d", response.statusCode()));
            //BridgeMod.LOGGER.info(response.body());

        } catch (Exception e) {
            BridgeMod.LOGGER.info("Exception");
            e.printStackTrace();
        }
    }
}
